package com.learning.awspring.repositories;

import com.learning.awspring.entities.InBoundLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InBoundLogRepository extends JpaRepository<InBoundLog, Long> {}
