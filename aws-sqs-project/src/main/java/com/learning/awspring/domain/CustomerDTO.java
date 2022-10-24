package com.learning.awspring.domain;

import jakarta.validation.constraints.NotBlank;

public record CustomerDTO(@NotBlank(message = "Text cannot be empty") String text) {}
