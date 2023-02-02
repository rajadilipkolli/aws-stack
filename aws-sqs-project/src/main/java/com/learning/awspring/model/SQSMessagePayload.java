package com.learning.awspring.model;

import java.io.Serializable;

public record SQSMessagePayload(String id, String messageBody) implements Serializable {}
