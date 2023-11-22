package com.learning.awslambda.services;

import com.learning.awslambda.entities.Actor;
import com.learning.awslambda.exception.ActorNotFoundException;
import com.learning.awslambda.mapper.ActorMapper;
import com.learning.awslambda.model.response.ActorResponse;
import com.learning.awslambda.repositories.ActorRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ActorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorService.class);

    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    public ActorService(ActorRepository actorRepository, ActorMapper actorMapper) {
        this.actorRepository = actorRepository;
        this.actorMapper = actorMapper;
    }

    public List<ActorResponse> findActorByName(String name) {
        LOGGER.info("Finding Actors By Name :{}", name);
        List<Actor> actorList = actorRepository.findByNameLike(name);
        if (actorList.isEmpty()) {
            throw new ActorNotFoundException(name);
        } else {
            return actorMapper.toResponseList(actorList);
        }
    }
}
