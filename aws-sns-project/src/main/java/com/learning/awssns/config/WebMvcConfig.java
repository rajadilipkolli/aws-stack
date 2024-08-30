package com.learning.awssns.config;

import static io.awspring.cloud.sns.configuration.NotificationHandlerMethodArgumentResolverConfigurationUtils.getNotificationHandlerMethodArgumentResolver;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class WebMvcConfig implements WebMvcConfigurer {

    private final ApplicationProperties properties;
    private final SnsClient snsClient;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(properties.getCors().getPathPattern())
                .allowedMethods(properties.getCors().getAllowedMethods())
                .allowedHeaders(properties.getCors().getAllowedHeaders())
                .allowedOriginPatterns(properties.getCors().getAllowedOriginPatterns())
                .allowCredentials(properties.getCors().isAllowCredentials());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(getNotificationHandlerMethodArgumentResolver(snsClient));
    }
}
