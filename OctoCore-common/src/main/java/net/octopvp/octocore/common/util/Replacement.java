package net.octopvp.octocore.common.util;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Replacement {
    private Map<Object, Object> replacements = new HashMap<>();
    private String message;

    public Replacement(String message) {
        this.message = message;
    }

    public Replacement add(Object current, Object replacement) {
        replacements.put(current, replacement);
        return this;
    }

    public String toString() {
        replacements.keySet().forEach(current -> this.message = this.message.replace(String.valueOf(current), String.valueOf(replacements.get(current))));
        return ChatColor.translateAlternateColorCodes('&',this.message);
    }

    public String toString(boolean ignored) {
        replacements.keySet().forEach(current -> this.message = this.message.replace(String.valueOf(current), String.valueOf(replacements.get(current))));
        return this.message;
    }
}