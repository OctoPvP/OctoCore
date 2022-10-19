package net.octopvp.aetheriacoremaster.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "apikeys")
@AllArgsConstructor
@Getter
@Setter
public class APIKey {
    @Id
    private String id;
    private long createdOn;
    @DBRef
    private UserModel user;

    public String getCreatedDate() {
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(createdOn));
    }
}
