package com.spartamarket.controller;

import com.spartamarket.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class ViewController {

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    @GetMapping("/api/product")
    public String productPage() {
        return "products";
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
