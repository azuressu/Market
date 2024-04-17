package com.spartamarket.filter;

import com.spartamarket.entity.UserRoleEnum;
import com.spartamarket.jwt.JwtUtil;
import com.spartamarket.jwt.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // username과 password 가져오기
        /* 아래의 코드 혹은 LoginRequestDto를 이용해서
           LoginRequestDto dto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
         */
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // Spring Security에서 username과 password를 검증하기 위해 token에 담아주어야 함
        UsernamePasswordAuthenticationToken authenticationToken
                 = new UsernamePasswordAuthenticationToken(username, password, null);

        // token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authenticationToken);
    }

    // 성공 시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response
                                            , FilterChain filterChain, Authentication authentication) {
        // UserDetails  생성
        String username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authentication.getPrincipal()).getUser().getRole();

        // token 가져오기
        String token = jwtUtil.createToken(username, role);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        logger.info("로그인 성공" + token);
    }

    // 실패 시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) {
        response.setStatus(401);
        /* 협업 시, 클라이언트 쪽에 추가적인 메세지 등이나 데이터를 넘겨주세요 라고한다면
        * 여기서 status만 설정하는 것이 아니라
        * response 객체, content-type 그리고 메시지 등을 담아서 보낼 수 있음
        * */
    }



}
