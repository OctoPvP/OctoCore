package net.octopvp.octocore.master.repository;

import net.octopvp.octocore.master.models.Setting;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SettingRepository extends MongoRepository<Setting, String> {
    Setting findByKey(String key);

    List<Setting> findByValue(String value);

    void delete(Setting setting);
}
