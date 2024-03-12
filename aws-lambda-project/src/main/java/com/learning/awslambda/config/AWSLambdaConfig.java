package com.learning.awslambda.config;

import com.learning.awslambda.model.request.ActorRequest;
import com.learning.awslambda.model.response.ActorResponse;
import com.learning.awslambda.services.ActorService;
import java.util.List;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AWSLambdaConfig {

    @Bean
    Function<ActorRequest, List<ActorResponse>> findActorByName(ActorService actorService) {
        return request -> actorService.findActorByName(request.name());
    }
}
