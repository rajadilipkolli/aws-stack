package com.learning.awslambda.web.controllers;

import com.learning.awslambda.model.response.ActorResponse;
import com.learning.awslambda.services.ActorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<ActorResponse> getActorByName(@PathVariable String name) {
        return ResponseEntity.ok(actorService.findActorByName(name));
    }
}
