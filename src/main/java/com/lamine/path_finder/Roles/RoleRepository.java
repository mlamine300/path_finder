package com.lamine.path_finder.Roles;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role,String> {
    Optional<Role>findRoleByName(String name);


}
