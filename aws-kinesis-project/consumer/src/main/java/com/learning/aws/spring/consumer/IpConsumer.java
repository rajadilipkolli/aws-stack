package com.learning.aws.spring.consumer;

import com.learning.aws.spring.config.ApplicationProperties;
import com.learning.aws.spring.entities.IpAddressEvent;
import com.learning.aws.spring.model.IpAddressDTO;
import com.learning.aws.spring.repository.IpAddressEventRepository;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.kinesis.retrieval.KinesisClientRecord;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
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

    public ParallelFlux<IpAddressEvent> process(KinesisClientRecord kinesisClientRecord) {
        return Flux.just(kinesisClientRecord)
                .flatMap(
                        kinesisRecord -> {
                            log.info(
                                    "Sequence Number :{}, partitionKey :{} and expected ArrivalTime :{}",
                                    kinesisRecord.sequenceNumber(),
                                    kinesisRecord.partitionKey(),
                                    kinesisRecord.approximateArrivalTimestamp());

                            String dataAsString =
                                    new String(BinaryUtils.copyBytesFrom(kinesisRecord.data()));
                            String payload = dataAsString.substring(dataAsString.indexOf("[{"));

                            try {
                                List<IpAddressDTO> ipAddressDTOS =
                                        objectMapper.readValue(payload, new TypeReference<>() {});
                                return Flux.fromIterable(ipAddressDTOS);
                            } catch (JacksonException e) {
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
                                    .flatMap(ipAddressEventRepository::save)
                                    .retryWhen(
                                            Retry.backoff(
                                                    3,
                                                    Duration.ofSeconds(
                                                            1))) // Retry strategy for save errors
                                    .onErrorContinue(
                                            (throwable, obj) ->
                                                    log.error(
                                                            "Error processing record: {}",
                                                            obj,
                                                            throwable)); // Continue on error
                        });
    }
}
