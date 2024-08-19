package com.spartamarket.service;

import com.spartamarket.dto.JoinRequestDto;
import com.spartamarket.entity.User;
import com.spartamarket.entity.UserRoleEnum;
import com.spartamarket.jwt.JwtUtil;
import com.spartamarket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private JwtUtil jwtUtil;

    @Value("${spring.admin.key}")
    private String adminToken;

    @Test
    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        userService = new UserService(userRepository, passwordEncoder, jwtUtil);
    }
    
    @Test
    @DisplayName("일반 회원가입 성공")
    public void JoinTest_General() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("highlight");
        joinRequestDto.setPassword("highlight00");
        joinRequestDto.setNickname("light");

        // when
        userService.joinSpartaMarket(joinRequestDto);

        // then
        System.out.println("일반 회원가입 성공");
    }

    @Test
    @DisplayName("관리자 회원가입 성공")
    public void JoinTest_Admin() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("admin");
        joinRequestDto.setPassword("adminadmin");
        joinRequestDto.setNickname("admin");
        joinRequestDto.setAdmin(true);
        joinRequestDto.setAdminToken(adminToken);

        // when
        userService.setAdminToken(adminToken);
        userService.joinSpartaMarket(joinRequestDto);

        // then
        System.out.println("ADMIN 회원가입 성공");
    }

    @Test
    @DisplayName("일반 회원가입 실패 - username 오류")
    public void JoinTest_ExceptionAboutUsername() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("hi");
        joinRequestDto.setPassword("lightlight");
        joinRequestDto.setNickname("light");

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.joinSpartaMarket(joinRequestDto);
        });

        // then
        assertEquals("아이디 형식에 맞지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("일반 회원가입 실패 - password 오류")
    public void JoinTest_ExceptionAboutPassword() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("highlight");
        joinRequestDto.setPassword("hihi");
        joinRequestDto.setNickname("light");

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.joinSpartaMarket(joinRequestDto);
        });

        // then
        assertEquals("비밀번호 형식에 맞지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("관리자 회원가입 실패 - token 오류")
    public void JoinTest_ExceptionAboutAdminToken() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("admin");
        joinRequestDto.setPassword("adminadmin");
        joinRequestDto.setNickname("admin");
        joinRequestDto.setAdmin(true);
        joinRequestDto.setAdminToken("아무거나넣기");
        userService.setAdminToken(adminToken);

        // when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.joinSpartaMarket(joinRequestDto);
        });

        // then
        assertEquals("잘못된 Admin Token", exception.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 사용자 이름")
    public void Duplicatied_Username() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("highlight");
        joinRequestDto.setPassword("highlight00");
        joinRequestDto.setNickname("light");

        String password = passwordEncoder.encode(joinRequestDto.getPassword());

        User newUser = new User(joinRequestDto, password, UserRoleEnum.USER);

        // when
        when(userRepository.existsByUsername(any(String.class))).thenReturn(true);
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.joinSpartaMarket(joinRequestDto);
        });

        // then
        assertEquals("중복된 사용자 이름", exception.getMessage());
    }


}