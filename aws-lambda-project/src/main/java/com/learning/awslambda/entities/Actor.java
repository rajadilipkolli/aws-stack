package com.learning.awslambda.entities;

import java.util.Objects;

public class Actor {

    private Long id;

    private String name;

    public Actor(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Actor() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Actor actor = (Actor) o;
        return id != null && Objects.equals(id, actor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
