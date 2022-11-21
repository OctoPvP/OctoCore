package net.octopvp.octocore.master.repository;

import net.octopvp.octocore.master.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoUserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
    Boolean existsByUsernameIgnoreCase(String username);

    Optional<User> findByUserID(String userID);
}
