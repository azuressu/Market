package com.spartamarket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ViewController {

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    @GetMapping("/api/join")
    public String joinPage() {
        return "join";
    }

    @GetMapping("/api/login")
    public String loginPage() {
        return "login";
    }

}
