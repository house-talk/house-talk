package com.team.house.housetalk.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * 🔥 JWT 필터를 적용하지 않을 경로
     * - OAuth 인증 과정
     * - 에러 페이지
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/oauth2")
                || path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1️⃣ 쿠키에서 JWT 추출
        String token = resolveTokenFromCookie(request);

        // 2️⃣ 토큰이 존재하고 유효하다면
        if (token != null && jwtProvider.validateToken(token)) {

            // 3️⃣ 토큰에서 adminId 추출
            Long adminId = jwtProvider.getAdminId(token);

            // 4️⃣ 인증 객체 생성
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            adminId,                // principal
                            null,                   // credentials
                            Collections.emptyList() // 권한 (추후 ROLE_ADMIN)
                    );

            // 5️⃣ 인증 상세 정보 설정
            ((UsernamePasswordAuthenticationToken) authentication)
                    .setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

            // 6️⃣ SecurityContext에 인증 저장
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        // 7️⃣ 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    /**
     * 🍪 Cookie에서 accessToken 추출
     */
    private String resolveTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if ("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
