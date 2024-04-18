package com.spartamarket.entity;

import com.spartamarket.dto.JoinRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

// lombok
@Getter
@NoArgsConstructor

// jpa
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 15)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private LocalDateTime joindate;

    public User(JoinRequestDto joinRequestDto, String password, UserRoleEnum role) {
        this.username = joinRequestDto.getUsername();
        this.password = password;
        this.nickname = joinRequestDto.getNickname();
        this.role = role;
        this.joindate = LocalDateTime.now();
    }


}
