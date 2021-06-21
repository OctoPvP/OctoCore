package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Settings {
    private Map<String,String> settingsMap = new ConcurrentHashMap<>();
    public String get(String key){
        return settingsMap.get(key);
    }
    public void set(String key,String value){
        settingsMap.put(key, value);
    }
}

