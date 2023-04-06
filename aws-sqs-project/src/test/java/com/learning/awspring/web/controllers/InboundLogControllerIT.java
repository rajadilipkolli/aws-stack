package com.learning.awspring.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awspring.common.AbstractIntegrationTest;
import com.learning.awspring.entities.InboundLog;
import com.learning.awspring.repositories.InboundLogRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

class InboundLogControllerIT extends AbstractIntegrationTest {

    @Autowired private InboundLogRepository inboundLogRepository;

    private List<InboundLog> inboundLogList = null;

    @BeforeEach
    void setUp() {
        inboundLogRepository.deleteAllInBatch();

        inboundLogList = new ArrayList<>();
        inboundLogList.add(
                new InboundLog(
                        null,
                        "First InboundLog",
                        """
                        {"id": "string1","messageBody": "string1"}
                        """,
                        null,
                        null));
        inboundLogList.add(
                new InboundLog(
                        null,
                        "Second InboundLog",
                        """
                        {"id": "string2","messageBody": "string2"}
                        """,
                        null,
                        null));
        inboundLogList.add(
                new InboundLog(
                        null,
                        "Third InboundLog",
                        """
                        {"id": "string3","messageBody": "string3"}
                        """,
                        null,
                        null));
        inboundLogList = inboundLogRepository.saveAll(inboundLogList);
    }

    @Test
    void shouldFetchAllInboundLogs() throws Exception {
        this.mockMvc
                .perform(get("/api/inboundlog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()", is(inboundLogList.size())))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.isFirst", is(true)))
                .andExpect(jsonPath("$.isLast", is(true)))
                .andExpect(jsonPath("$.hasNext", is(false)))
                .andExpect(jsonPath("$.hasPrevious", is(false)));
    }

    @Test
    void shouldFindInboundLogById() throws Exception {
        InboundLog inboundLog = inboundLogList.get(0);
        Long inboundLogId = inboundLog.getId();

        this.mockMvc
                .perform(get("/api/inboundlog/{id}", inboundLogId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId", is(inboundLog.getMessageId())));
    }

    @Test
    void shouldCreateNewInboundLog() throws Exception {
        InboundLog inboundLog =
                new InboundLog(
                        null,
                        "New InboundLog",
                        """
                       {"id": "string1","messageBody": "string1"}
                        """,
                        null,
                        null);
        this.mockMvc
                .perform(
                        post("/api/inboundlog")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inboundLog)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.messageId", is(inboundLog.getMessageId())));
    }

    @Test
    void shouldReturn400WhenCreateNewInboundLogWithoutMessageId() throws Exception {
        InboundLog inboundLog = new InboundLog(null, null, null, null, null);

        this.mockMvc
                .perform(
                        post("/api/inboundlog")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inboundLog)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/inboundlog")))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("messageId")))
                .andExpect(jsonPath("$.violations[0].message", is("MessageId cannot be blank")))
                .andReturn();
    }

    @Test
    void shouldUpdateInboundLog() throws Exception {
        InboundLog inboundLog = inboundLogList.get(0);
        inboundLog.setMessageId("Updated InboundLog");

        this.mockMvc
                .perform(
                        put("/api/inboundlog/{id}", inboundLog.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inboundLog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId", is(inboundLog.getMessageId())));
    }

    @Test
    void shouldDeleteInboundLog() throws Exception {
        InboundLog inboundLog = inboundLogList.get(0);

        this.mockMvc
                .perform(delete("/api/inboundlog/{id}", inboundLog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId", is(inboundLog.getMessageId())));
    }
}
