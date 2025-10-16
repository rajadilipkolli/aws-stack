package com.learning.awspring.web.controllers;

import com.learning.awspring.entities.InboundLog;
import com.learning.awspring.model.response.PagedResult;
import com.learning.awspring.services.InboundLogService;
import com.learning.awspring.utils.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inboundlog")
public class InboundLogController {

    private static final Logger log = LoggerFactory.getLogger(InboundLogController.class);
    private final InboundLogService inboundLogService;

    public InboundLogController(InboundLogService inboundLogService) {
        this.inboundLogService = inboundLogService;
    }

    @GetMapping
    public PagedResult<InboundLog> getAllInboundLogs(
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
                    int pageNo,
            @RequestParam(defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
                    int pageSize,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
                    String sortBy,
            @RequestParam(defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
                    String sortDir) {
        return inboundLogService.findAllInboundLogs(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InboundLog> getInboundLogById(@PathVariable Long id) {
        return inboundLogService
                .findInboundLogById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InboundLog createInboundLog(@RequestBody @Validated InboundLog inboundLog) {
        return inboundLogService.saveInboundLog(inboundLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InboundLog> updateInboundLog(
            @PathVariable Long id, @RequestBody InboundLog inboundLog) {
        return inboundLogService
                .findInboundLogById(id)
                .map(
                        inboundLogObj -> {
                            inboundLog.setId(id);
                            return ResponseEntity.ok(inboundLogService.saveInboundLog(inboundLog));
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InboundLog> deleteInboundLog(@PathVariable Long id) {
        return inboundLogService
                .findInboundLogById(id)
                .map(
                        inboundLog -> {
                            inboundLogService.deleteInboundLogById(id);
                            return ResponseEntity.ok(inboundLog);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
