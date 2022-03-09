package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Text {

    private String text, hover;

    public Text(String t) {
        this.text = t;
    }

    public Text(String t, String hover) {

    }
}
