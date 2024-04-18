package com.spartamarket.controller;

import com.spartamarket.service.UserService;
import com.spartamarket.dto.JoinRequestDto;
import com.spartamarket.dto.StatusResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<StatusResponseDto> joinSpartaMarket(@Valid @RequestBody JoinRequestDto joinRequestDto, BindingResult bindingResult) {
        try {
            userService.joinSpartaMarket(joinRequestDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(201).body(new StatusResponseDto("회원가입 성공", HttpStatus.CREATED.value()));
    }
}
