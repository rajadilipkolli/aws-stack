package com.example.awsspring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.awsspring.common.AbstractIntegrationTest;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricStat;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

class ApplicationIntegrationTest extends AbstractIntegrationTest {

    @Autowired private CloudWatchAsyncClient cloudWatchAsyncClient;

    @Test
    void contextLoads() throws Exception {
        Instant startTime = Instant.now();
        Instant endTime = startTime.plus(Duration.ofSeconds(30));

        for (int i = 0; i < 5; i++) {
            this.mockMvc
                    .perform(get("/api/customers"))
                    .andExpectAll(
                            status().isOk(), content().contentType(MediaType.APPLICATION_JSON));
        }

        Dimension error = Dimension.builder().name("error").value("none").build();
        Dimension exception = Dimension.builder().name("exception").value("none").build();
        Dimension method = Dimension.builder().name("method").value("GET").build();
        Dimension outcome = Dimension.builder().name("outcome").value("SUCCESS").build();
        Dimension uri = Dimension.builder().name("uri").value("/api/customers").build();
        Dimension status = Dimension.builder().name("status").value("200").build();
        Metric metric =
                Metric.builder()
                        .namespace("tc-localstack")
                        .metricName("http.server.requests.count")
                        .dimensions(error, exception, method, outcome, uri, status)
                        .build();
        MetricStat metricStat =
                MetricStat.builder()
                        .stat("Maximum")
                        .metric(metric)
                        .unit(StandardUnit.COUNT)
                        .period(5)
                        .build();
        MetricDataQuery metricDataQuery =
                MetricDataQuery.builder()
                        .metricStat(metricStat)
                        .id("test1")
                        .returnData(true)
                        .build();
        await().atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofSeconds(5))
                .ignoreExceptions()
                .untilAsserted(
                        () -> {
                            GetMetricDataResponse response =
                                    this.cloudWatchAsyncClient
                                            .getMetricData(
                                                    GetMetricDataRequest.builder()
                                                            .startTime(startTime)
                                                            .endTime(endTime)
                                                            .metricDataQueries(metricDataQuery)
                                                            .build())
                                            .get();
                            assertThat(response.metricDataResults()).hasSize(1);
                            assertThat(response.metricDataResults().get(0).label())
                                    .isEqualTo("http.server.requests.count");
                            assertThat(response.metricDataResults().get(0).values()).contains(5d);
                        });
    }
}
