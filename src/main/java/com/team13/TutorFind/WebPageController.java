package com.team13.TutorFind;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// Source - https://stackoverflow.com/a
// Posted by m4rtin, modified by community. See post 'Timeline' for change history
// Retrieved 2025-12-05, License - CC BY-SA 4.0


@Controller
public class WebPageController {

    @RequestMapping("/")
    public String index(){ return "redirect:/home"; }

    @RequestMapping("/home")
    public String home(){ return "index"; }

    @RequestMapping("/home/about")
    public String about(){
        return "other/about";
    }
}
