package com.learning.aws.spring.model;

import java.time.LocalDateTime;

public record IpAddressDTO(String ipAddress, LocalDateTime eventProducedTime) {}
