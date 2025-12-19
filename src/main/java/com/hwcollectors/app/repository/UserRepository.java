package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByKeycloakId(String keycloakId);
}

