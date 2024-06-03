package com.spartamarket.config;

import com.spartamarket.filter.JwtFilter;
import com.spartamarket.filter.LoginFilter;
import com.spartamarket.jwt.JwtUtil;
import com.spartamarket.jwt.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration     // configuration 클래스 등록
@EnableWebSecurity // web security 활성화
@RequiredArgsConstructor
public class WebSecurityConfig {

    /**
     * SecurityConfig
     * 스프링 세큐리티의 인가 및 설정을 담당하는 클래스
     * SecurityConfig 구현은 Security 버전별로 상이하므로 주의하기
     */

    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter(jwtUtil);
        loginFilter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return loginFilter;
    }

    /**
     * 비밀번호 인코딩 메소드 빈 등록
     * BCrypt 해싱 알고리즘을 사용해 비밀번호를 안전하게 해싱
     * 이렇게 함으로써 사용자의 비밀번호가 안전하게 저장되고, 보안을 향상시킴
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 세큐리티 필터 체인 빈 등록
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // CSRF Disable
        httpSecurity.csrf((auth) -> auth.disable());

        // Form 로그인 방식 Disable
        httpSecurity.formLogin((auth) -> auth.disable());

        // Http Basic 인증 방식 Disable
        httpSecurity.httpBasic((auth) -> auth.disable());

        // 경로별로 인가 작업
        httpSecurity
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/join").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/products").permitAll()
                        .anyRequest().authenticated());

        httpSecurity
                .addFilterBefore(jwtFilter(), LoginFilter.class);

        httpSecurity
                .addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);

        /**
         * 세션 설정
         * JWT를 통한 인증/인가를 위해 세션을 STATELESS 상태로 설정하는 것이 중요
         *
            * 확장성 - 서버의 부하를 줄이고 확작성 향상
            * 분산 클라이언트 환경에서의 유용성 - 분산 시스템에서 세션 동기화 문제 해결
            * 클라이언트 측 저장소 활용 - 클라이언트 측에서도 상태를 유지할 수 있어 효율적
            * 보안 - 세션 데이터를 서버에 저장하지 않고도 보안 유지 가능
         */
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }






}
