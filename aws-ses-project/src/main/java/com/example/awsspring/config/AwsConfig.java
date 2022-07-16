package com.example.awsspring.config;

import static com.example.awsspring.utils.AppConstants.PROFILE_NOT_TEST;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import io.awspring.cloud.autoconfigure.context.properties.AwsCredentialsProperties;
import io.awspring.cloud.ses.SimpleEmailServiceJavaMailSender;
import io.awspring.cloud.ses.SimpleEmailServiceMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@Profile({PROFILE_NOT_TEST})
@RequiredArgsConstructor
public class AwsConfig {
    private final ApplicationProperties properties;
    private final AwsCredentialsProperties awsCredentialsProperties;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
    }

    @Bean
    @Primary
    public AmazonS3 amazonS3Client() {

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().enablePathStyleAccess();
        if (properties.getEndpointUri() != null
                && properties.getEndpointUri().trim().length() != 0) {
            builder.withEndpointConfiguration(getEndpointConfiguration());
            builder.withCredentials(getCredentialsProvider());
        }
        return builder.build();
    }

    @Bean
    @Primary
    public AmazonSimpleEmailService amazonSimpleEmailService() {

        AmazonSimpleEmailServiceClientBuilder builder =
                AmazonSimpleEmailServiceClientBuilder.standard();
        if (properties.getEndpointUri() != null
                && properties.getEndpointUri().trim().length() != 0) {
            builder.withEndpointConfiguration(getEndpointConfiguration());
            builder.withCredentials(getCredentialsProvider());
        }
        return builder.build();
    }

    @Bean
    public JavaMailSender javaMailSender(AmazonSimpleEmailService amazonSimpleEmailService) {
        return new SimpleEmailServiceJavaMailSender(amazonSimpleEmailService);
    }

    @Bean
    public MailSender mailSender(AmazonSimpleEmailService amazonSimpleEmailService) {
        return new SimpleEmailServiceMailSender(amazonSimpleEmailService);
    }

    private AWSCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(
                        awsCredentialsProperties.getAccessKey(),
                        awsCredentialsProperties.getSecretKey()));
    }

    private AwsClientBuilder.EndpointConfiguration getEndpointConfiguration() {
        return new AwsClientBuilder.EndpointConfiguration(
                properties.getEndpointUri(), properties.getRegion());
    }
}
