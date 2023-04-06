package com.learning.awspring.repositories;

import com.learning.awspring.entities.InboundLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InboundLogRepository extends JpaRepository<InboundLog, Long> {}
