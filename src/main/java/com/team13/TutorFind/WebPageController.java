package com.team13.TutorFind;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


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
