package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
public class User {

    @JsonAlias({"id"})
    private Integer id;

    @Setter
    @JsonAlias({"username"})
    private String username;

    @Setter
    @JsonAlias({"password"})
    private String password;

    @Setter
    @JsonAlias({"coins"})
    private Integer coins;

    @Setter
    @JsonAlias({"name"})
    private String name;

    @Setter
    @JsonAlias({"bio"})
    private String bio;

    @Setter
    @JsonAlias({"image"})
    private String image;

    // Constructor with @JsonCreator and @JsonProperty annotations for deserialization
    public User(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User() {

    }
}
