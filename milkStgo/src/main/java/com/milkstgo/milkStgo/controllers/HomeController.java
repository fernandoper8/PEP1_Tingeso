package com.milkstgo.milkStgo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.PublicKey;

@Controller
@RequestMapping
public class HomeController {
    @GetMapping("/")
    public String main(){
        return "main";
    }
}
