package com.team.house.housetalk.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

    /**
     * 로그아웃 API
     *
     * - JWT 쿠키 만료 처리
     * - 인증 상태와 무관하게 호출 가능
     */
    @PostMapping("/api/auth/logout")
    public void logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // 배포 시 true
        cookie.setPath("/");
        cookie.setMaxAge(0);     // 즉시 만료

        response.addCookie(cookie);
    }

    @GetMapping("/api/auth/check")
    public void checkAuth(
            Authentication authentication,
            HttpServletRequest request
    ) {
        // 1️⃣ 관리자 인증 (JWT)
        if (authentication != null && authentication.isAuthenticated()) {
            return;
        }

        // 2️⃣ 세입자 인증 (tenantCode 쿠키)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("tenantCode".equals(cookie.getName())
                        && cookie.getValue() != null
                        && !cookie.getValue().isBlank()) {
                    return;
                }
            }
        }

        // ❌ 인증 실패
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
