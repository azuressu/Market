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

    @Value("${spartamarket.admin.key}")
    private String realAdminToken;

    public void joinSpartaMarket(JoinRequestDto joinRequestDto) {
        Boolean admin = joinRequestDto.getAdmin();
        UserRoleEnum roleEnum = UserRoleEnum.USER;

        if (userRepository.existsByUsername(joinRequestDto.getUsername())) {
            System.out.println("중복된 사용자 이름");
            throw new IllegalArgumentException("중복된 사용자 이름");
        }

        String adminToken;

        // 만약 admin이 true 이면,
        if (admin) {
            adminToken = joinRequestDto.getAdminToken();
            if (adminToken.equals(realAdminToken)) {
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
}
