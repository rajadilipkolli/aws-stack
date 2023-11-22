package com.learning.awslambda.mapper;

import com.learning.awslambda.entities.Actor;
import com.learning.awslambda.model.request.ActorRequest;
import com.learning.awslambda.model.response.ActorResponse;
import org.springframework.stereotype.Service;

@Service
public class ActorMapper {

    public Actor toEntity(ActorRequest actorRequest) {
        Actor actor = new Actor();
        actor.setName(actorRequest.name());
        return actor;
    }

    public ActorResponse toResponse(Actor actor) {
        return new ActorResponse(actor.getId(), actor.getName());
    }
}
