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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.kinesis.model.Record;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IpConsumer {

    private final ObjectMapper objectMapper;
    private final IpAddressEventRepository ipAddressEventRepository;

    // As we are using useNativeDecoding = true along with the listenerMode = batch,
    // there is no any out-of-the-box conversion happened and a result message contains a payload
    // like List<software.amazon.awssdk.services.kinesis.model.Record>. hence manually manipulating
    @Bean
    public Consumer<Flux<List<Record>>> consumeEvent() {
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

                                    String utf8String = kinessRecord.data().asUtf8String();
                                    String substring =
                                            utf8String.substring(utf8String.indexOf("[{"));
                                    List<IpAddressDTO> ipAddressDTOS;
                                    try {
                                        ipAddressDTOS =
                                                objectMapper.readValue(
                                                        substring, new TypeReference<>() {});
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
