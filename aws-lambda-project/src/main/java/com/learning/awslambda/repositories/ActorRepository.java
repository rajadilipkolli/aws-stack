package com.learning.awslambda.repositories;

import com.learning.awslambda.entities.Actor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ActorRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActorRepository.class);

    private final JdbcClient jdbcClient;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ActorRepository(JdbcClient jdbcClient, NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Optional<Actor> findByNameLike(String name) {
        String sql = "select id, name from actors where name like :name";
        return jdbcClient.sql(sql).param("name", name).query(Actor.class).optional();
    }

    @Transactional
    public void deleteAll() {
        String sql = "delete from actors";
        int count = jdbcClient.sql(sql).update();
        LOGGER.info("deleted {} rows", count);
    }

    @Transactional
    public List<Actor> saveAll(List<Actor> actorList) {

        List<Object[]> batch = actorList.stream()
                .map(customer -> new Object[] {customer.getName()})
                .toList();

        String sql = "INSERT INTO actors(name) values :batch RETURNING id, name";

        return jdbcTemplate.query(sql, new MapSqlParameterSource("batch", batch), rs -> {
            List<Actor> actors = new ArrayList<>();
            while (rs.next()) {
                actors.add(new Actor(rs.getLong(1), rs.getString(2)));
            }
            return actors;
        });
    }
}
