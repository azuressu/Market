package com.spartamarket.filter;

import com.spartamarket.jwt.JwtUtil;
import com.spartamarket.jwt.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("JWT 검증 시도");
        String token = jwtUtil.getJwtFromCookie(request); // getTokenFromHeader()
        logger.info("JWT: " + token);

        if (StringUtils.hasText(token)) {
            token = jwtUtil.subStringToken(token); // Bearer 떼어버리기
            logger.info(token);

            if (!jwtUtil.validateToken(token)) { // 토큰 검증 결과가 false 라면, 메소드 종료 필수
                logger.error("Token Error");
                return;
            }

            Claims claim = jwtUtil.getUserInfoFromToken(token);

            try {
                setAuthentication(claim.getSubject());
            } catch (Exception e) {
                logger.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    } // doFilterInternal

    // 인증 처리 메소드
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);

        context.setAuthentication(authentication);
        /**
         * username > user 조회 > userDetails 담기 > authentication의 principal에 담기,
         * > securityContent에 담기 > SecurityContextHolder 에 담기
         * > 이제 ? @AuthenticationPrincipal 로 조회 가능
         */
        SecurityContextHolder.setContext(context);

    }


    // 인증 객체 생성 메소드
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
