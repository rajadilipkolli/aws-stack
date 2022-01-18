package com.learning.awspring.config;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.messaging.support.destination.DynamicQueueUrlDestinationResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AwsMessagingConfig {

  // @Bean annotation tells that a method produces a bean that is to be managed by the spring
  // container.
  @Bean
  public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
    DynamicQueueUrlDestinationResolver destinationResolver =
        new DynamicQueueUrlDestinationResolver(amazonSQSAsync);
    destinationResolver.setAutoCreate(true);
    return new QueueMessagingTemplate(amazonSQSAsync, destinationResolver, null);
  }
}
