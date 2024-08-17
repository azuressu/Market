package com.spartamarket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ViewController {

    /**
     * 메인 페이지
     * 메인 페이지를 리턴하는 메서드
     * @return : index.html 파일 리턴
     */
    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    /**
     * 회원가입 페이지
     * 회원가입 페이지를 리턴하는 메서드
     * @return : join.html 파일 리턴
     */
    @GetMapping("/api/join")
    public String joinPage() {
        return "join";
    }

    /**
     * 로그인 페이지
     * 로그인 페이지를 리턴하는 메서드
     * @return : login.html 파일 리턴
     */
    @GetMapping("/api/login")
    public String loginPage() {
        return "login";
    }

}
