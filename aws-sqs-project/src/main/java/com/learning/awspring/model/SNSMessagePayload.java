package com.learning.awspring.model;

import java.io.Serializable;

public record SNSMessagePayload(String id, String messageBody) implements Serializable {}
