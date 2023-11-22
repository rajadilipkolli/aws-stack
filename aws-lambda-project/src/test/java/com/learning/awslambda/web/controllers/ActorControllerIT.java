package com.learning.awslambda.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.learning.awslambda.common.AbstractIntegrationTest;
import com.learning.awslambda.entities.Actor;
import com.learning.awslambda.repositories.ActorRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ActorControllerIT extends AbstractIntegrationTest {

    @Autowired
    private ActorRepository actorRepository;

    private List<Actor> actorList = null;

    @BeforeEach
    void setUp() {
        actorRepository.deleteAll();

        actorList = new ArrayList<>();
        actorList.add(new Actor(null, "First Actor"));
        actorList.add(new Actor(null, "Second Actor"));
        actorList.add(new Actor(null, "Third Actor"));
        actorList = actorRepository.saveAll(actorList);
    }

    @Test
    void shouldFindActorById() throws Exception {
        Actor actor = actorList.get(0);
        String actorName = actor.getName();

        this.mockMvc
                .perform(get("/api/actors/{name}", actorName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(actor.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(actor.getName())));
    }
}
