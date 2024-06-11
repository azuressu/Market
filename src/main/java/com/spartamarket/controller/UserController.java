package com.spartamarket.controller;

import com.spartamarket.dto.JoinRequestDto;
import com.spartamarket.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/join")
    public String joinSpartaMarket(@Valid JoinRequestDto joinRequestDto) {
        try {
            userService.joinSpartaMarket(joinRequestDto);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return "redirect:/api/login";
        // return ResponseEntity.status(201).body(new StatusResponseDto("회원가입 성공", HttpStatus.CREATED.value()));
    }
}
