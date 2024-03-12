package com.learning.awslambda.exception;

public class ActorNotFoundException extends ResourceNotFoundException {

    public ActorNotFoundException(String name) {
        super("Actor with Name '%s' not found".formatted(name));
    }
}
