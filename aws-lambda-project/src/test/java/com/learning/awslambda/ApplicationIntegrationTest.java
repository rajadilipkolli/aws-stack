package com.learning.awslambda;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.ResourceReaper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.CreateFunctionUrlConfigRequest;
import software.amazon.awssdk.services.lambda.model.CreateFunctionUrlConfigResponse;
import software.amazon.awssdk.services.lambda.model.Environment;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.FunctionUrlAuthType;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationResponse;
import software.amazon.awssdk.services.lambda.model.PackageType;
import software.amazon.awssdk.services.lambda.model.Runtime;

@Testcontainers(parallel = true)
class ApplicationIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationIntegrationTest.class);

    static String jar = buildJar();

    static Network network = Network.newNetwork();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2-alpine")
            .withNetwork(network)
            .withNetworkAliases("postgres")
            .withReuse(true);

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack").withTag("3.3.0"))
            .withNetwork(network)
            .withEnv("LOCALSTACK_HOST", "localhost.localstack.cloud")
            .withEnv("LAMBDA_DOCKER_NETWORK", ((Network.NetworkImpl) network).getName())
            .withEnv("LAMBDA_RUNTIME_ENVIRONMENT_TIMEOUT", "30")
            .withNetworkAliases("localstack")
            .withEnv("LAMBDA_DOCKER_FLAGS", testContainersLabels())
            .withReuse(true);

    private static String testContainersLabels() {
        return Stream.of(
                        DockerClientFactory.DEFAULT_LABELS.entrySet().stream(),
                        ResourceReaper.instance().getLabels().entrySet().stream())
                .flatMap(Function.identity())
                .map(entry -> "-l %s=%s".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(" "));
    }

    @Test
    void contextLoads() throws IOException {

        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(LOGGER);
        localstack.followOutput(logConsumer);
        postgres.followOutput(logConsumer);

        String fnName = "findActorByName-fn";
        Map<String, String> envVars = Map.ofEntries(
                Map.entry("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres:5432/test"),
                Map.entry("SPRING_DATASOURCE_USERNAME", postgres.getUsername()),
                Map.entry("SPRING_DATASOURCE_PASSWORD", postgres.getPassword()));
        CreateFunctionRequest createFunctionRequest = CreateFunctionRequest.builder()
                .functionName(fnName)
                .runtime(Runtime.JAVA21)
                .role("arn:aws:iam::123456789012:role/irrelevant")
                .packageType(PackageType.ZIP)
                .code(FunctionCode.builder()
                        .zipFile(SdkBytes.fromByteArray(FileUtils.readFileToByteArray(new File(jar))))
                        .build())
                .timeout(10)
                .handler("org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest")
                .environment(Environment.builder().variables(envVars).build())
                .description("Spring Cloud Function AWS Adapter Example")
                .build();

        LambdaClient lambdaClient = LambdaClient.builder()
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .endpointOverride(localstack.getEndpoint())
                .build();

        CreateFunctionResponse createFunctionResponse = lambdaClient.createFunction(createFunctionRequest);
        LOGGER.info("createFunctionResponse :{}", createFunctionResponse);
        WaiterResponse<GetFunctionConfigurationResponse> waiterResponse = lambdaClient
                .waiter()
                .waitUntilFunctionActive(GetFunctionConfigurationRequest.builder()
                        .functionName(fnName)
                        .build());
        LOGGER.info("waiterResponse :{}", waiterResponse);

        CreateFunctionUrlConfigRequest createFunctionUrlConfigRequest = CreateFunctionUrlConfigRequest.builder()
                .functionName(fnName)
                .authType(FunctionUrlAuthType.NONE)
                .build();
        CreateFunctionUrlConfigResponse createFunctionUrlConfigResponse =
                lambdaClient.createFunctionUrlConfig(createFunctionUrlConfigRequest);

        String functionUrl =
                createFunctionUrlConfigResponse.functionUrl().replace("" + 4566, "" + localstack.getMappedPort(4566));
        LOGGER.info("functionURL :{}", functionUrl);
        var responseBody = RestAssured.given()
                .body("""
                {"name": "profile"}
                """)
                .get(functionUrl)
                .prettyPeek()
                .andReturn()
                .body();
        assertThat(responseBody.asString())
                .isEqualToIgnoringWhitespace(
                        """
                [{"id":1,"name":"profile-1"},{"id":2,"name":"profile-2"},{"id":3,"name":"profile-3"},{"id":4,"name":"profile-4"}]
                """);
        responseBody = RestAssured.given()
                .body("""
                {"name": "junit"}
                """)
                .get(functionUrl)
                .prettyPeek()
                .andReturn()
                .body();
        assertThat(responseBody.asString()).isEqualTo("[]");
    }

    private static String buildJar() {
        try {
            var properties = new Properties();
            properties.setProperty("skipTests", "true");

            var defaultInvocationRequest = new DefaultInvocationRequest();
            if (StringUtils.hasText(System.getenv("MAVEN_HOME"))) {
                defaultInvocationRequest.setMavenHome(new File(System.getenv("MAVEN_HOME")));
            }
            defaultInvocationRequest.setJavaHome(new File(System.getenv("JAVA_HOME")));
            defaultInvocationRequest.setPomFile(Paths.get("pom.xml").toFile());
            defaultInvocationRequest.setGoals(List.of("package"));
            defaultInvocationRequest.setProperties(properties);
            var appInvoker = new DefaultInvoker();
            appInvoker.execute(defaultInvocationRequest);
        } catch (MavenInvocationException e) {
            throw new RuntimeException("Could not build jar", e);
        }
        return "target/aws-lambda-project-0.0.1-SNAPSHOT-aws.jar";
    }
}
