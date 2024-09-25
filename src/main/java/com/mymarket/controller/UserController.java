package com.mymarket.controller;

import com.mymarket.dto.JoinRequestDto;
import com.mymarket.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * 회원가입을 진행하는 메서드
     * @param joinRequestDto : 회원가입에 필요한 정보 (username, password, nickname, email)
     * @param request : HttpServletRequest
     * @param response : HttpServletResponse
     * @return : 회원가입 이후 로그인 페이지로 이동할 주소
     */
    @PostMapping("/api/join")
    public String joinSpartaMarket(@Valid JoinRequestDto joinRequestDto,
                                   HttpServletRequest request, HttpServletResponse response) {
        userService.deleteToken(request, response);

        try {
            userService.joinSpartaMarket(joinRequestDto);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return "redirect:/api/login";
        // return ResponseEntity.status(201).body(new StatusResponseDto("회원가입 성공", HttpStatus.CREATED.value()));
    }
}
