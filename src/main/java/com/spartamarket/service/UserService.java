package com.spartamarket.service;

import com.spartamarket.dto.JoinRequestDto;
import com.spartamarket.entity.User;
import com.spartamarket.entity.UserRoleEnum;
import com.spartamarket.jwt.JwtUtil;
import com.spartamarket.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.admin.key}")
    private String savedAdminToken;

    private final JwtUtil jwtUtil;

    /**
     * 토큰 삭제
     * 토큰을 삭제하는 메서드
     * @param request : HttpServletRequest 요청 내용
     * @param response : HttpServletResponse 응답 내용
     */
    public void deleteToken(HttpServletRequest request, HttpServletResponse response) {
        jwtUtil.deleteCookie(request, response);
    }

    /**
     * 회원가입
     * 회원가입을 진행하는 메서드
     * @param joinRequestDto : 회원가입 시 필요한 정보 (username, password, nickname, email)
     */
    public void joinSpartaMarket(JoinRequestDto joinRequestDto) {
        // 관리자 확인 여부 admin 변수 확인
        Boolean admin = joinRequestDto.getAdmin();
        // 기본 사용자의 Role은 USER로 설정
        UserRoleEnum roleEnum = UserRoleEnum.USER;

        // 이미 존재하는 username 이라면
        if (userRepository.existsByUsername(joinRequestDto.getUsername())) {
            System.out.println("중복된 사용자 이름");
            throw new IllegalArgumentException("중복된 사용자 이름");
        }

        // username 유효성 형식과 맞지 않다면
        if (!joinRequestDto.getUsername().matches("^[a-z0-9]{4,10}$")) {
            throw new IllegalArgumentException("아이디 형식에 맞지 않습니다.");
        }

        // password 유효성 형식과 맞지 않다면
        if (!joinRequestDto.getPassword().matches("^[a-zA-Z0-9#?!@$%^&*-]{8,15}$")) {
            throw new IllegalArgumentException("비밀번호 형식에 맞지 않습니다.");
        }

        // 만약 admin이 true 이면 (관리자로 회원가입을 시도한다면)
        if (admin) {
            String adminToken = joinRequestDto.getAdminToken();
            log.info("저장된 admin : " + savedAdminToken);
            log.info("입력받은 admin : " + adminToken);
            if (savedAdminToken.equals(adminToken)) {
                roleEnum = UserRoleEnum.ADMIN;
            } else {
                throw new IllegalArgumentException("잘못된 Admin Token");
            }
        }

        // 비밀번호 인코딩
        String password = passwordEncoder.encode(joinRequestDto.getPassword());

        User user = new User(joinRequestDto, password, roleEnum);
        userRepository.save(user);

        log.info("회원가입 성공");
    }

    /**
     * 관리자 토큰 설정
     * 관리자 토큰을 설정하는 메서드
     * @param adminToken : 입력받은 관리자 토큰
     */
    public void setAdminToken(String adminToken) {
        this.savedAdminToken = adminToken;
    }
}
