package com.spartamarket.filter;

import com.spartamarket.entity.UserRoleEnum;
import com.spartamarket.jwt.JwtUtil;
import com.spartamarket.jwt.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public LoginFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String jwt = jwtUtil.getJwtFromCookie(request);
        if (StringUtils.hasText(jwt)) {
            System.out.println("Cookie안의 JWT 인식 = " + jwt);
            jwtUtil.deleteCookie(request, response);
        }

        // username과 password 가져오기
        logger.info("로그인 시도");
        logger.info(obtainUsername(request));
        logger.info(obtainPassword(request));

        /*try {
            LoginRequestDto loginDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            String username = loginDto.getUsername();
            String password = loginDto.getPassword(); */
            String username = obtainUsername(request);
            String password = obtainPassword(request);

            // Spring Security에서 username과 password를 검증하기 위해 token에 담아주어야 함
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(
                    username, password, null);

            logger.info("token principal = " + authenticationToken.getPrincipal());
            logger.info("token credentials = " + authenticationToken.getCredentials());
            logger.info("token authorities = " + authenticationToken.getAuthorities());

            logger.info("username = " + username + " / password = " + password);

            Authentication authentication = getAuthenticationManager().authenticate(authenticationToken);

            // token에 담은 검증을 위한 AuthenticationManager로 전달
            return authentication;
        /*} catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }*/
    }

    // 성공 시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response
                                            , FilterChain filterChain, Authentication authentication) throws IOException {
        // UserDetails  생성
        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getRole();

        // token 가져오기
        String token = jwtUtil.createToken(username, role);
        jwtUtil.addJwtToCookie(token, response);

        response.sendRedirect("/");
        logger.info("로그인 성공" + token);
    }

    // 실패 시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        logger.error("로그인 오류");
        response.setStatus(400);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.sendRedirect("/api/login");
    }



}
