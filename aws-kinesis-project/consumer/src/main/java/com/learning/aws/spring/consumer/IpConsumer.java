package com.learning.aws.spring.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.aws.spring.model.IpAddressDTO;
import java.time.LocalDateTime;
import java.util.List;
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
                                })
                        .subscribe();
    }
}
