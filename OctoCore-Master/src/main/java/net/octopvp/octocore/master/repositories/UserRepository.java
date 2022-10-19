package net.octopvp.aetheriacoremaster.repositories;

import net.octopvp.aetheriacoremaster.models.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserModel, String> {
    Optional<UserModel> findById(String id);

    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByEmail(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
