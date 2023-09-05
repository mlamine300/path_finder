package com.lamine.path_finder.Entities;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EntityRepository extends MongoRepository<Entity,String> {
    public Optional<Entity> findEntityByName(String name);
}
