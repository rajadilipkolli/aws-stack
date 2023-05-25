package com.learning.awspring.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("application")
public class ApplicationProperties {

    @NotBlank(message = "endpoint URI Can't be Blank")
    private String endpointUri;

    private String region;
}
