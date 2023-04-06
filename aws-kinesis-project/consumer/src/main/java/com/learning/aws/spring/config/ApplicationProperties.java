package com.learning.aws.spring.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.validation.constraints.NotBlank;

@Data
@ConfigurationProperties("application")
public class ApplicationProperties {

    @NotBlank(message = "AWS Endpoint URL Cant be Null")
    private String endpointUri;

    @NotBlank(message = "AWS Region Cant be Null")
    private String region;

    @NestedConfigurationProperty private Cors cors = new Cors();

    @Data
    public static class Cors {
        private String pathPattern = "/api/**";
        private String allowedMethods = "*";
        private String allowedHeaders = "*";
        private String allowedOriginPatterns = "*";
        private boolean allowCredentials = true;
    }
}
