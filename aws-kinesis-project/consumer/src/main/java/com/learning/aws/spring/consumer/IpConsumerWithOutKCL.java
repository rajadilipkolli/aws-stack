package com.learning.aws.spring.consumer;

import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        value = "spring.cloud.stream.kinesis.binder.kpl-kcl-enabled",
        havingValue = "false")
public class IpConsumerWithOutKCL {

    private static final Logger log = LoggerFactory.getLogger(IpConsumerWithOutKCL.class);

    private final IpConsumer ipConsumer;

    public IpConsumerWithOutKCL(IpConsumer ipConsumer) {
        this.ipConsumer = ipConsumer;
    }

    // As we are using useNativeDecoding = true along with the listenerMode = batch,
    // there is no any out-of-the-box conversion happened and a result message contains a payload
    // like List<software.amazon.awssdk.services.kinesis.model.Record>. hence manually manipulating
    @Bean
    Consumer<Flux<List<Record>>> consumeEvent() {
        return listFlux ->
                listFlux.flatMapIterable(records -> records)
                        .map(KinesisClientRecord::fromRecord)
                        .flatMap(ipConsumer::process)
                        .subscribe(savedEvent -> log.info("Saved Event :{}", savedEvent));
    }
}
