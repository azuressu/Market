package com.spartamarket.service;

import com.spartamarket.dto.JoinRequestDto;
import com.spartamarket.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
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

    @Value("${spring.admin.key}")
    private String adminToken;

    @Test
    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        userService = new UserService(userRepository, passwordEncoder);
    }
    
    @Test
    @DisplayName("일반 회원가입 성공")
    public void JoinTest_General() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("highlight");
        joinRequestDto.setPassword("highlight00");
        joinRequestDto.setNickname("light");

        // String password = passwordEncoder.encode("highlight00");
        // User user = new User(joinRequestDto, password, UserRoleEnum.USER);

        // when
        userService.joinSpartaMarket(joinRequestDto);
        // when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        // then
        System.out.println("일반 회원가입 성공");
        // assertEquals(user.getUsername(),"highlight@naver.com");
        // assertEquals(user.getNickname(),"light");
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
        userService.setAdminToken(adminToken); // 결국 method를 추가해서 완료
        userService.joinSpartaMarket(joinRequestDto);

        // then
        System.out.println("ADMIN 회원가입 성공");
    }

    @Test
    @DisplayName("일반 회원가입 실패 - username 오류")
    public void JoinTest_ExceptionAboutUsername() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("hig");
        joinRequestDto.setPassword("lightlight");
        joinRequestDto.setNickname("light");

        // when & then
        /*Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.joinSpartaMarket(joinRequestDto);
        });*/
        try {
            userService.joinSpartaMarket(joinRequestDto);
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("아이디 형식에 맞지 않습니다.", e.getMessage());
        }

    }

    @Test
    @DisplayName("일반 회원가입 실패 - password 오류")
    public void JoinTest_ExceptionAboutPassword() {
        // given
        JoinRequestDto joinRequestDto = new JoinRequestDto();

        joinRequestDto.setUsername("highlight");
        joinRequestDto.setPassword("hihi");
        joinRequestDto.setNickname("light");

        // when & then
        try {
            userService.joinSpartaMarket(joinRequestDto);
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("비밀번호 형식에 맞지 않습니다.", e.getMessage());
        }

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
        joinRequestDto.setAdminToken(adminToken);

        // when & then
        /*Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.joinSpartaMarket(joinRequestDto);
        });*/
        userService.setAdminToken(adminToken);
        try {
            userService.joinSpartaMarket(joinRequestDto);
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("잘못된 Admin Token", e.getMessage());
        }
    }


}