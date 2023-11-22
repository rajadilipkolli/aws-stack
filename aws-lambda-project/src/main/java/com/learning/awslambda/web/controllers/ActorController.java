package com.learning.awslambda.web.controllers;

import com.learning.awslambda.exception.ActorNotFoundException;
import com.learning.awslambda.model.response.ActorResponse;
import com.learning.awslambda.services.ActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    @GetMapping("/{name}")
    public ResponseEntity<ActorResponse> getActorByName(@PathVariable String name) {
        return actorService
                .findActorByName(name)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ActorNotFoundException(name));
    }
}
