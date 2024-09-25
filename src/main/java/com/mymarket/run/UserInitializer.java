package com.mymarket.run;

import com.mymarket.dto.JoinRequestDto;
import com.mymarket.entity.User;
import com.mymarket.entity.UserRoleEnum;
import com.mymarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String password = passwordEncoder.encode("rawPassword00");

        List<User> newUsers = new ArrayList<>();

        for (String username: usernameList) {
            JoinRequestDto joinRequestDto = new JoinRequestDto();
            joinRequestDto.setUsername(username);
            joinRequestDto.setNickname(username);
            joinRequestDto.setEmail(username + "@gmail.com");

            User user = new User(
                    joinRequestDto, password, UserRoleEnum.USER
            );

            newUsers.add(user);
        }

        userRepository.saveAll(newUsers);
    }

    private List<String> usernameList = Arrays.asList(
            "jn8c5z", "q3x8vop", "w4z6y", "a1p3f", "u7k2b4",
            "d9f1q", "l7v3yz2", "c5m2q", "b8n4t", "x1r7y2",
            "v6p9t", "h5n3xl7", "m8k5v", "r4x1j7", "f2g6z",
            "q7n2j4", "s3v8n", "z4l7p", "t9k1x2", "p5m3y",
            "n6j4v", "y8r2c5", "e3w7z", "g1f6x", "k4n9t",
            "u8m2j", "w9x1r", "h7q5y", "l2f8v", "v3k6p"
    );

}
