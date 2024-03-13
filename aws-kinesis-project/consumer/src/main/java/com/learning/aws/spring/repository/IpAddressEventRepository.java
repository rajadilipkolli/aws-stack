package com.learning.aws.spring.repository;

import com.learning.aws.spring.entities.IpAddressEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface IpAddressEventRepository extends ReactiveCrudRepository<IpAddressEvent, Long> {}
