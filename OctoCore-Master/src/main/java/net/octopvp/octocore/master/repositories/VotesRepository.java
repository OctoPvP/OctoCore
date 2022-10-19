package net.octopvp.aetheriacoremaster.repositories;

import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.models.VoteModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VotesRepository extends MongoRepository<VoteModel, String> {
    @Deprecated
    List<VoteModel> findByUsername(String username);

    List<UserModel> findByUuid(UUID uuid);

    void delete(VoteModel voteModel);
}
