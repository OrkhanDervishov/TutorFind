package com.team13.TutorFind;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller
//public class HomeController {
//
////    @RequestMapping("/")
////    public String index(){ return "redirect:/home"; }
////
////    @RequestMapping("/home")
////    public String home(){
////        return "home.html";
////    }
//
//    @RequestMapping("/{page}")
//    public String pages(@PathVariable String page){
//        return page + ".html";
//    }
//
//    @RequestMapping("/tutors/{page}")
//    public String tutorpages(@PathVariable String page){
//        return page + ".html";
//    }
//}