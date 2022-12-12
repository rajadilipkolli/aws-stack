package com.learning.awspring.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

import com.learning.awspring.entities.InboundLog;
import com.learning.awspring.model.response.PagedResult;
import com.learning.awspring.repositories.InboundLogRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class InboundLogServiceTest {

    @Mock private InboundLogRepository inboundLogRepository;

    @InjectMocks private InboundLogService inboundLogService;

    @Test
    void findAllInboundLogs() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<InboundLog> inboundLogPage = new PageImpl<>(List.of(getInboundLog()));
        given(inboundLogRepository.findAll(pageable)).willReturn(inboundLogPage);

        // when
        PagedResult<InboundLog> pagedResult =
                inboundLogService.findAllInboundLogs(0, 10, "id", "asc");

        // then
        assertThat(pagedResult).isNotNull();
        assertThat(pagedResult.data()).isNotEmpty().hasSize(1);
        assertThat(pagedResult.hasNext()).isFalse();
        assertThat(pagedResult.pageNumber()).isEqualTo(1);
        assertThat(pagedResult.totalPages()).isEqualTo(1);
        assertThat(pagedResult.isFirst()).isTrue();
        assertThat(pagedResult.isLast()).isTrue();
        assertThat(pagedResult.hasPrevious()).isFalse();
        assertThat(pagedResult.totalElements()).isEqualTo(1);
    }

    @Test
    void findInboundLogById() {
        // given
        given(inboundLogRepository.findById(1L)).willReturn(Optional.of(getInboundLog()));
        // when
        Optional<InboundLog> optionalInboundLog = inboundLogService.findInboundLogById(1L);
        // then
        assertThat(optionalInboundLog).isPresent();
        InboundLog inboundLog = optionalInboundLog.get();
        assertThat(inboundLog.getId()).isEqualTo(1L);
        assertThat(inboundLog.getMessageId()).isEqualTo("junitTest");
    }

    @Test
    void saveInboundLog() {
        // given
        given(inboundLogRepository.save(getInboundLog())).willReturn(getInboundLog());
        // when
        InboundLog persistedInboundLog = inboundLogService.saveInboundLog(getInboundLog());
        // then
        assertThat(persistedInboundLog).isNotNull();
        assertThat(persistedInboundLog.getId()).isEqualTo(1L);
        assertThat(persistedInboundLog.getMessageId()).isEqualTo("junitTest");
    }

    @Test
    void deleteInboundLogById() {
        // given
        willDoNothing().given(inboundLogRepository).deleteById(1L);
        // when
        inboundLogService.deleteInboundLogById(1L);
        // then
        verify(inboundLogRepository, times(1)).deleteById(1L);
    }

    private InboundLog getInboundLog() {
        InboundLog inboundLog = new InboundLog();
        inboundLog.setId(1L);
        inboundLog.setMessageId("junitTest");
        return inboundLog;
    }
}
