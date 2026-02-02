package com.learning.awslambda;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.ResourceReaper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Testcontainers(parallel = true)
class ApplicationIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationIntegrationTest.class);

    static String jar = buildJar();

    static Network network = Network.newNetwork();

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.1-alpine")
            .withNetwork(network)
            .withNetworkAliases("postgres")
            .withReuse(true);

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack").withTag("4.13.1"))
            .withNetwork(network)
            .withEnv("LOCALSTACK_HOST", "localhost.localstack.cloud")
            .withEnv("LAMBDA_DOCKER_NETWORK", ((Network.NetworkImpl) network).getName())
            .withEnv("LAMBDA_RUNTIME_ENVIRONMENT_TIMEOUT", "60")
            .withEnv("LAMBDA_REMOVE_CONTAINERS", "false")
            .withEnv("LAMBDA_TRUNCATE_STDOUT", "2000")
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

        File jarFile = new File(jar);
        long jarSizeInMB = jarFile.length() / (1024 * 1024);
        LOGGER.info("JAR file size: {} MB", jarSizeInMB);

        LambdaClient lambdaClient = LambdaClient.builder()
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .endpointOverride(localstack.getEndpoint())
                .build();

        FunctionCode functionCode;

        // If JAR is larger than 50MB, use S3 upload; otherwise use direct ZIP upload
        if (jarSizeInMB > 50) {
            LOGGER.info("JAR size exceeds 50MB, using S3 upload");

            // Create S3 client
            S3Client s3Client = S3Client.builder()
                    .region(Region.of(localstack.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                    .endpointOverride(localstack.getEndpoint())
                    .build();

            // Create bucket
            String bucketName = "lambda-deployment-bucket";
            String key = "lambda-functions/" + fnName + ".jar";

            try {
                s3Client.createBucket(
                        CreateBucketRequest.builder().bucket(bucketName).build());
                LOGGER.info("Created S3 bucket: {}", bucketName);
            } catch (Exception e) {
                LOGGER.info("Bucket might already exist: {}", e.getMessage());
            }

            // Upload JAR to S3
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(key).build(), RequestBody.fromFile(jarFile));

            LOGGER.info("Uploaded JAR to S3: s3://{}/{}", bucketName, key);

            functionCode =
                    FunctionCode.builder().s3Bucket(bucketName).s3Key(key).build();
        } else {
            LOGGER.info("Using direct ZIP upload");
            functionCode = FunctionCode.builder()
                    .zipFile(SdkBytes.fromByteArray(FileUtils.readFileToByteArray(jarFile)))
                    .build();
        }

        CreateFunctionRequest createFunctionRequest = CreateFunctionRequest.builder()
                .functionName(fnName)
                .runtime(Runtime.JAVA25)
                .role("arn:aws:iam::123456789012:role/irrelevant")
                .packageType(PackageType.ZIP)
                .code(functionCode)
                .timeout(30)
                .memorySize(512)
                .handler("org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest")
                .environment(Environment.builder().variables(envVars).build())
                .description("Spring Cloud Function AWS Adapter Example")
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
            defaultInvocationRequest.setPomFile(Path.of("pom.xml").toFile());
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
