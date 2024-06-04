package com.spartamarket.service;

import com.spartamarket.dto.JoinRequestDto;
import com.spartamarket.entity.User;
import com.spartamarket.entity.UserRoleEnum;
import com.spartamarket.repository.UserRepository;
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

    public void joinSpartaMarket(JoinRequestDto joinRequestDto) {
        Boolean admin = joinRequestDto.getAdmin();
        UserRoleEnum roleEnum = UserRoleEnum.USER;

        if (userRepository.existsByUsername(joinRequestDto.getUsername())) {
            System.out.println("중복된 사용자 이름");
            throw new IllegalArgumentException("중복된 사용자 이름");
        }

        if (!joinRequestDto.getUsername().matches("^[a-z0-9]{4,10}$")) {
            throw new IllegalArgumentException("아이디 형식에 맞지 않습니다.");
        }

        if (!joinRequestDto.getPassword().matches("^[a-zA-Z0-9#?!@$%^&*-]{8,15}$")) {
            throw new IllegalArgumentException("비밀번호 형식에 맞지 않습니다.");
        }

        // 만약 admin이 true 이면,
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

        String password = passwordEncoder.encode(joinRequestDto.getPassword());

        User user = new User(joinRequestDto, password, roleEnum);
        userRepository.save(user);

        log.info("회원가입 성공");
    }

    public void setAdminToken(String adminToken) {
        this.savedAdminToken = adminToken;
    }
}
