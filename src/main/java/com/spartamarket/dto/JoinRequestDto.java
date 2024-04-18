package com.spartamarket.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JoinRequestDto {

    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "최소 4자 이상 10자 이하이며, 알파벳 소문자(a-z) 및 숫자(0-9)로 구성 필수")
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9#?!@$%^&*-]{8,15}$", message = "최소 8자 이상 15자 이하이며, 알파벳 대소문자(A-Z, a-z) 및 숫자(0-9) 및 특수문자로 구성 필수")
    private String password;

    private String nickname;

    private Boolean admin = false;   // 기본을 admin이 아닌 일반 회원으로 설정
    private String adminToken = "";  // 기본값 역시 빈 String으로 설정
}
