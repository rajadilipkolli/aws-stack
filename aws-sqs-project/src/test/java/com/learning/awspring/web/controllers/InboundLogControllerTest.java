package com.learning.awspring.web.controllers;

import static com.learning.awspring.utils.AppConstants.PROFILE_TEST;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.awspring.entities.InboundLog;
import com.learning.awspring.model.response.PagedResult;
import com.learning.awspring.services.InboundLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = InboundLogController.class)
@ActiveProfiles(PROFILE_TEST)
class InboundLogControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private InboundLogService inboundLogService;

    @Autowired private ObjectMapper objectMapper;

    private List<InboundLog> inboundLogList;

    @BeforeEach
    void setUp() {
        this.inboundLogList = new ArrayList<>();
        this.inboundLogList.add(new InboundLog(1L, "text 1", null, null, null));
        this.inboundLogList.add(new InboundLog(2L, "text 2", null, null, null));
        this.inboundLogList.add(new InboundLog(3L, "text 3", null, null, null));
    }

    @Test
    void shouldFetchAllInboundLogs() throws Exception {
        Page<InboundLog> page = new PageImpl<>(inboundLogList);
        PagedResult<InboundLog> inboundLogPagedResult = new PagedResult<>(page);
        given(inboundLogService.findAllInboundLogs(0, 10, "id", "asc"))
                .willReturn(inboundLogPagedResult);

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
        Long inboundLogId = 1L;
        InboundLog inboundLog = new InboundLog(inboundLogId, "text 1", null, null, null);
        given(inboundLogService.findInboundLogById(inboundLogId))
                .willReturn(Optional.of(inboundLog));

        this.mockMvc
                .perform(get("/api/inboundlog/{id}", inboundLogId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId", is(inboundLog.getMessageId())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingInboundLog() throws Exception {
        Long inboundLogId = 1L;
        given(inboundLogService.findInboundLogById(inboundLogId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(get("/api/inboundlog/{id}", inboundLogId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewInboundLog() throws Exception {
        given(inboundLogService.saveInboundLog(any(InboundLog.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        InboundLog inboundLog = new InboundLog(1L, "some text", null, null, null);
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
        Long inboundLogId = 1L;
        InboundLog inboundLog = new InboundLog(inboundLogId, "Updated text", null, null, null);
        given(inboundLogService.findInboundLogById(inboundLogId))
                .willReturn(Optional.of(inboundLog));
        given(inboundLogService.saveInboundLog(any(InboundLog.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/inboundlog/{id}", inboundLog.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inboundLog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId", is(inboundLog.getMessageId())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingInboundLog() throws Exception {
        Long inboundLogId = 1L;
        given(inboundLogService.findInboundLogById(inboundLogId)).willReturn(Optional.empty());
        InboundLog inboundLog = new InboundLog(inboundLogId, "Updated text", null, null, null);

        this.mockMvc
                .perform(
                        put("/api/inboundlog/{id}", inboundLogId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inboundLog)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteInboundLog() throws Exception {
        Long inboundLogId = 1L;
        InboundLog inboundLog = new InboundLog(inboundLogId, "Some text", null, null, null);
        given(inboundLogService.findInboundLogById(inboundLogId))
                .willReturn(Optional.of(inboundLog));
        doNothing().when(inboundLogService).deleteInboundLogById(inboundLog.getId());

        this.mockMvc
                .perform(delete("/api/inboundlog/{id}", inboundLog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId", is(inboundLog.getMessageId())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingInboundLog() throws Exception {
        Long inboundLogId = 1L;
        given(inboundLogService.findInboundLogById(inboundLogId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(delete("/api/inboundlog/{id}", inboundLogId))
                .andExpect(status().isNotFound());
    }
}
