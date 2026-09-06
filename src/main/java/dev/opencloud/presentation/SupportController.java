package dev.opencloud.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportController {

    @GetMapping("/support")
    public String support() {
        return "support/support";
    }

}
