package net.octopvp.octocore.master.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.interfaces.ISetting;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "settings")
@Getter
@Setter
@AllArgsConstructor
public class Setting implements ISetting {
    @Id
    private String key;
    private String value;
}
