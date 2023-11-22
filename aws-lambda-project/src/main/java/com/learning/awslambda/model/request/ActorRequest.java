package com.learning.awslambda.model.request;

import jakarta.validation.constraints.NotEmpty;

public record ActorRequest(@NotEmpty(message = "Name cannot be empty") String name) {}
