package com.learning.aws.spring.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.aws.spring.config.ApplicationProperties;
import com.learning.aws.spring.entities.IpAddressEvent;
import com.learning.aws.spring.model.IpAddressDTO;
import com.learning.aws.spring.repository.IpAddressEventRepository;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.kinesis.model.Record;

@Configuration(proxyBeanMethods = false)
public class IpConsumer {

    private static final Logger log = LoggerFactory.getLogger(IpConsumer.class);

    private final ObjectMapper objectMapper;
    private final IpAddressEventRepository ipAddressEventRepository;
    private final ApplicationProperties applicationProperties;

    public IpConsumer(
            ObjectMapper objectMapper,
            IpAddressEventRepository ipAddressEventRepository,
            ApplicationProperties applicationProperties) {
        this.objectMapper = objectMapper;
        this.ipAddressEventRepository = ipAddressEventRepository;
        this.applicationProperties = applicationProperties;
    }

    // As we are using useNativeDecoding = true along with the listenerMode = batch,
    // there is no any out-of-the-box conversion happened and a result message contains a payload
    // like List<software.amazon.awssdk.services.kinesis.model.Record>. hence manually manipulating
    @Bean
    Consumer<Flux<List<Record>>> consumeEvent() {
        return recordFlux ->
                recordFlux
                        .flatMapIterable(list -> list)
                        .flatMap(
                                kinesisRecord -> {
                                    log.info(
                                            "Sequence Number :{}, partitionKey :{} and expected ArrivalTime :{}",
                                            kinesisRecord.sequenceNumber(),
                                            kinesisRecord.partitionKey(),
                                            kinesisRecord.approximateArrivalTimestamp());

                                    String dataAsString =
                                            new String(kinesisRecord.data().asByteArray());
                                    String payload =
                                            dataAsString.substring(dataAsString.indexOf("[{"));

                                    try {
                                        List<IpAddressDTO> ipAddressDTOS =
                                                objectMapper.readValue(
                                                        payload, new TypeReference<>() {});
                                        return Flux.fromIterable(ipAddressDTOS);
                                    } catch (JsonProcessingException e) {
                                        return Flux.error(e);
                                    }
                                })
                        .parallel() // Parallelize processing
                        .runOn(Schedulers.boundedElastic()) // Run processing on boundedElastic
                        // Scheduler
                        .flatMap(
                                ipAddressDTO -> {
                                    IpAddressEvent ipAddressEvent =
                                            new IpAddressEvent(
                                                    ipAddressDTO.ipAddress(),
                                                    ipAddressDTO.eventProducedTime());
                                    return Mono.just(ipAddressEvent)
                                            .delayElement(
                                                    Duration.ofSeconds(
                                                            applicationProperties
                                                                    .getEventProcessingDelaySeconds())) // Adds artificial latency
                                            .flatMap(ipAddressEventRepository::save);
                                })
                        .subscribe(savedEvent -> log.info("Saved Event :{}", savedEvent));
    }
}
