package com.learning.awslambda.repositories;

import com.learning.awslambda.entities.Actor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    Optional<Actor> findByNameLike(String name);
}
