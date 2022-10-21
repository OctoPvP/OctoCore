package net.octopvp.octocore.master.repository;

import net.octopvp.octocore.master.models.VoteModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VotesRepository extends MongoRepository<VoteModel, String> {
    @Deprecated
    List<VoteModel> findByUsername(String username);

    void delete(VoteModel voteModel);
}
