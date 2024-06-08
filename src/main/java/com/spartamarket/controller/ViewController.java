package com.spartamarket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api")
public class ViewController {

    @GetMapping("/main")
    public String mainPage() {
        return "index";
    }

    @GetMapping("/join")
    public String joinPage() {
        return "join";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


}
