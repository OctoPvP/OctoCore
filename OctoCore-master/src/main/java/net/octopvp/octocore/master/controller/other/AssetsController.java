package net.octopvp.octocore.master.controller.other;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assets")
public class AssetsController {
    private static final boolean dev = System.getProperty("dev", "false").equalsIgnoreCase("true");

    @GetMapping("/js/izitoast.js")
    public String eventbus() {
        if (dev)
            return "../static/dist/js/izitoast.js";
        else return "../static/dist/js/izitoast.min.js";
    }

    @GetMapping("/css/izitoast.css")
    public String eventbuscss() {
        return "../static/dist/css/izitoast.min.css";
    }

    @GetMapping("/css/bootstrap-datetimepicker.css")
    public String datetimepickercss() {
        if (true)
            return "../static/dist/css/bootstrap-datetimepicker.css";
        if (dev)
            return "../static/dist/css/bootstrap-datetimepicker.css";
        else return "../static/dist/css/bootstrap-datetimepicker.min.css";
    }
}
