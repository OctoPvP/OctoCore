package net.octopvp.aetheriacoremaster.repositories;

import net.octopvp.aetheriacoremaster.models.Setting;
import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.models.VoteModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface SettingRepository extends MongoRepository<Setting, String> {
    Setting findByKey(String key);

    List<Setting> findByValue(String value);

    void delete(Setting setting);
}
