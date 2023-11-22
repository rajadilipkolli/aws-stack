package com.learning.awslambda.services;

import com.learning.awslambda.mapper.ActorMapper;
import com.learning.awslambda.model.response.ActorResponse;
import com.learning.awslambda.repositories.ActorRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    public Optional<ActorResponse> findActorByName(String name) {
        return actorRepository.findByNameLike(name).map(actorMapper::toResponse);
    }
}
