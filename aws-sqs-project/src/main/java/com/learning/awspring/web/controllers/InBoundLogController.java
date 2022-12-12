package com.learning.awspring.web.controllers;

import com.learning.awspring.entities.InBoundLog;
import com.learning.awspring.model.PagedResult;
import com.learning.awspring.services.InBoundLogService;
import com.learning.awspring.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inbound")
@RequiredArgsConstructor
public class InBoundLogController {

    private final InBoundLogService inBoundLogService;

    @GetMapping
    public PagedResult<InBoundLog> getAllInBoundLogs(
            @RequestParam(
                            value = "pageNo",
                            defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,
                            required = false)
                    int pageNo,
            @RequestParam(
                            value = "pageSize",
                            defaultValue = AppConstants.DEFAULT_PAGE_SIZE,
                            required = false)
                    int pageSize,
            @RequestParam(
                            value = "sortBy",
                            defaultValue = AppConstants.DEFAULT_SORT_BY,
                            required = false)
                    String sortBy,
            @RequestParam(
                            value = "sortDir",
                            defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,
                            required = false)
                    String sortDir) {
        return inBoundLogService.findAllInBoundLogs(pageNo, pageSize, sortBy, sortDir);
    }
}
