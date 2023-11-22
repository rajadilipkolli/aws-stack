package com.learning.awslambda.services;

import com.learning.awslambda.exception.ActorNotFoundException;
import com.learning.awslambda.mapper.ActorMapper;
import com.learning.awslambda.model.response.ActorResponse;
import com.learning.awslambda.repositories.ActorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ActorService {

    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    public ActorService(ActorRepository actorRepository, ActorMapper actorMapper) {
        this.actorRepository = actorRepository;
        this.actorMapper = actorMapper;
    }

    public ActorResponse findActorByName(String name) {
        return actorRepository
                .findByNameLike(name)
                .map(actorMapper::toResponse)
                .orElseThrow(() -> new ActorNotFoundException(name));
    }
}
