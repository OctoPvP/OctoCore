package net.octopvp.aetheriacoremaster.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "master-users")
@Getter
@Setter
public class UserModel {
    public static final String DEFAULT_PROFILE_PICTURE = "iVBORw0KGgoAAAANSUhEUgAAAJYAAACWBAMAAADOL2zRAAAAFVBMVEXFxcX////7+/vv7+/e3t7R0dHJycnl4e1FAAADpUlEQVRo3u2YTVMbMQyG2+bjrndDz2ghPSeZgXPSaTlDoD1ngP//G9ox2ijG63gl57jvAEnI7DOSLMuyvowaNWrUqFGjRmX05+XX778XIb20TP91c3eoJU3XxPSh5qEO9c5MR+GuBjURiMC4AjZtKRZWXtRsTZ/VPjpZe2UwiRZOD6lP9y7WupfVHDxryNSrHw7WRkMFYtQYNmVByR/NNHtePFFOjTm3mLJ6dO2ey0R/R6paJ1sJNRMxE2qcnAqIuzWMwrfybUWVAnlhDReUgUCAvLcGjBNjVGwL2IzOCitzduHEyVjXBta3yLfUySvzZlQgxDJPdd1BYbGzH29bV0ntdxYHa0rkheFJMRdW3ratK71SFHwslocTPThPxtRHGCrFlNEbcQUbWFQyzGLXiQ2IklasW9WWCX9O5MUW1txgl30PcRR9WArrmhHCHhgSdYExiE17e3PMJw6/HD6AIAvZVjcmqhsD67VQJ5amsyPER/xjCZOvO5nmMkubAG8xZHy8hFWFtTnZnc17W8u6JxAklyA2aeFZWNvCgIC00YILbAm9MWCgXmHr7jG5iz6cPearbr5TZPjXlb8nZ9nZ6IjiotVJ9IStqbha1V+u5usEAu0ljHpLOUFL/21byoQAWc1yG6ZyjhV+xucFkpuoN8eE6J7DvHG1h6o9x8X+tmYyFAIlxR81Lj5rVnGlk8/FnPCj/LD3XIuJe8eoI6d2a6wSm3wjhxt7YuW1dHiYAQIWLzcn4e7DNvZqEzMYxyad723lOZpZyQfoEMzbyfkPkHmbVHn3zfYtc/9UPzF4nrlWjAZbQyc/zcEyRYOkBCvbOmzaCSaKF3PX3wPy6dbQ9rJYg9xCbKsTQrW0tRF8LjUab2dPjug/6WZWcz6vhDTCS8vNEemNm09dr3LRtJJ7E+uqsH8samwTbf/o6hsVxdEMcuicENJURikRD/wW/nkcGwI2C2YUOTwkKyaFcVyS/VgVwnWZDNtk3eOo1GLAoKKVJ4cLF9iMXAr+pLOeO1cglRoxCeUh5P5Y5xGdQwzhy9foChjyhXoX7BAKFCbvFBbwQbwwFgkkYSpm/jz7uEJSHQxTVV9tneiFM4Vml3JVLF6IQMkMWY8QXBdObI44alN6suWSYpeGu3tFPoCLDMugAotz63a+1y8WaCQkU6WYyRPRYZFiw4Y/QfNjL4v77cJpZ8dpS1dVvcqsiYPDmvh+lgouFoZ3h1/Jp+vM1vboe6ba17H+AetQVgc5T+AyAAAAAElFTkSuQmCC";

    @Id
    private String id;

    private String username;

    private String email;

    private String profilePicture;

    private Set<String> roles = new HashSet<>();

    private long lastLogin = -1;

    public UserModel() {
    }

    public UserModel(String username, String email) {
        this.username = username;
        this.email = email;
        this.profilePicture = DEFAULT_PROFILE_PICTURE;
    }

    public void onLogin() {
        this.lastLogin = System.currentTimeMillis();
    }
}
