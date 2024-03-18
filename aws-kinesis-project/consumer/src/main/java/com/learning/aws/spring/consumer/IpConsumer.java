package com.learning.aws.spring.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.aws.spring.entities.IpAddressEvent;
import com.learning.aws.spring.model.IpAddressDTO;
import com.learning.aws.spring.repository.IpAddressEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.kinesis.model.Record;

@Configuration(proxyBeanMethods = false)
public class IpConsumer {

    private static final Logger log = LoggerFactory.getLogger(IpConsumer.class);

    private final ObjectMapper objectMapper;
    private final IpAddressEventRepository ipAddressEventRepository;

    public IpConsumer(
            ObjectMapper objectMapper, IpAddressEventRepository ipAddressEventRepository) {
        this.objectMapper = objectMapper;
        this.ipAddressEventRepository = ipAddressEventRepository;
    }

    // As we are using useNativeDecoding = true along with the listenerMode = batch,
    // there is no any out-of-the-box conversion happened and a result message contains a payload
    // like List<software.amazon.awssdk.services.kinesis.model.Record>. hence manually manipulating
    @Bean
    Consumer<Flux<List<Record>>> consumeEvent() {
        return recordFlux ->
                recordFlux
                        .flatMap(Flux::fromIterable)
                        .map(
                                kinessRecord -> {
                                    log.info(
                                            "Sequence Number :{}, partitionKey :{} and expected ArrivalTime :{}",
                                            kinessRecord.sequenceNumber(),
                                            kinessRecord.partitionKey(),
                                            kinessRecord.approximateArrivalTimestamp());

                                    String dataAsString =
                                            new String(kinessRecord.data().asByteArray());
                                    String payload =
                                            dataAsString.substring(dataAsString.indexOf("[{"));
                                    List<IpAddressDTO> ipAddressDTOS;
                                    try {
                                        ipAddressDTOS =
                                                objectMapper.readValue(
                                                        payload, new TypeReference<>() {});
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return ipAddressDTOS;
                                })
                        .doOnNext(
                                ipAddressDTOsList -> {
                                    log.info(
                                            "IpAddress processed at {} and value is:{}",
                                            LocalDateTime.now(),
                                            ipAddressDTOsList);
                                    this.processEvents(ipAddressDTOsList);
                                })
                        .subscribe();
    }

    private void processEvents(List<IpAddressDTO> ipAddressDTOsList) {
        for (IpAddressDTO ipAddressDTO : ipAddressDTOsList) {
            insertToDB(ipAddressDTO);
        }
    }

    private void insertToDB(IpAddressDTO ipAddressDTO) {
        IpAddressEvent ipAddressEvent =
                new IpAddressEvent(ipAddressDTO.ipAddress(), ipAddressDTO.eventProducedTime());
        // adds artificial latency
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        ipAddressEventRepository
                .save(ipAddressEvent)
                .subscribe(savedEvent -> log.info("Saved Event :{}", savedEvent));
    }
}
