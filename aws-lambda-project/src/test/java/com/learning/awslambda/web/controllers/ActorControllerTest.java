package com.learning.awslambda.web.controllers;

import static com.learning.awslambda.utils.AppConstants.PROFILE_TEST;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.awslambda.entities.Actor;
import com.learning.awslambda.model.response.ActorResponse;
import com.learning.awslambda.services.ActorService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ActorController.class)
@ActiveProfiles(PROFILE_TEST)
class ActorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActorService actorService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Actor> actorList;

    @BeforeEach
    void setUp() {
        this.actorList = new ArrayList<>();
        this.actorList.add(new Actor(1L, "text 1"));
        this.actorList.add(new Actor(2L, "text 2"));
        this.actorList.add(new Actor(3L, "text 3"));
    }

    @Test
    void shouldFindActorById() throws Exception {
        String actorId = "text";
        ActorResponse actor = new ActorResponse(1L, "text 1");
        given(actorService.findActorByName(actorId)).willReturn(List.of(actor));

        this.mockMvc
                .perform(get("/api/actors/{name}", actorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(actor.name())));
    }
}
