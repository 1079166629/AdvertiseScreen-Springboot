package com.usr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller
//@ResponseBody
@RestController
public class HelloController {

    @RequestMapping("/hello")
//  @ResponseBody
    public String hello(){
        return "HelloSpringBoot";
    }
}
