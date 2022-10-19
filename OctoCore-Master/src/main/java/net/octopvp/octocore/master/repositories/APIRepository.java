package net.octopvp.aetheriacoremaster.repositories;

import net.octopvp.aetheriacoremaster.models.APIKey;
import net.octopvp.aetheriacoremaster.models.VoteModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface APIRepository extends MongoRepository<APIKey, String> {
    Optional<APIKey> findById(String id);

    List<APIKey> findByUser(User user);

    void delete(APIKey apiKey);
}
