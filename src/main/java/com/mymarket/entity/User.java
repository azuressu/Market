package com.mymarket.entity;

import com.mymarket.dto.JoinRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(JoinRequestDto joinRequestDto, String password, UserRoleEnum role) {
        this.username = joinRequestDto.getUsername();
        this.password = password;
        this.nickname = joinRequestDto.getNickname();
        this.email = joinRequestDto.getEmail();
        this.role = role;
    }


}
