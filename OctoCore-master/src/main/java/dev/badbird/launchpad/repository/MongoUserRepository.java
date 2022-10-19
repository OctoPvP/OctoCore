package dev.badbird.launchpad.repository;

import dev.badbird.launchpad.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoUserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<User> findByUserID(String userID);
}
