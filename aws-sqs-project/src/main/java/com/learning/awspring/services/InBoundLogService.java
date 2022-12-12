package com.learning.awspring.services;

import com.learning.awspring.entities.InBoundLog;
import com.learning.awspring.model.PagedResult;
import com.learning.awspring.repositories.InBoundLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InBoundLogService {

    private final InBoundLogRepository inBoundLogRepository;

    public PagedResult<InBoundLog> findAllInBoundLogs(
            int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort =
                sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<InBoundLog> inboundLogsPage = inBoundLogRepository.findAll(pageable);

        return new PagedResult<>(inboundLogsPage);
    }
}
