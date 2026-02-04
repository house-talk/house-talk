package com.team.house.housetalk.security.oauth;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import com.team.house.housetalk.security.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
        public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;
    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        System.out.println("🔥 OAuthLoginSuccessHandler 진입");
        System.out.println("🔥 redirectUri = [" + redirectUri + "]");


        // 1️⃣ OAuth 인증 토큰
        OAuth2AuthenticationToken authToken =
                (OAuth2AuthenticationToken) authentication;

        // 2️⃣ OAuth 사용자 정보
        OAuth2User oAuth2User = authToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 3️⃣ OAuth 제공자 식별자
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        // 4️⃣ OAuth2UserInfo 생성
        OAuth2UserInfo userInfo;
        try {
            userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                    registrationId,
                    attributes
            );
        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    e.getMessage()
            );
            return;
        }

        // 5️⃣ 필수 정보 검증
        if (userInfo.getProviderUserId() == null || userInfo.getProviderUserId().isBlank()
                || userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "OAuth 사용자 정보가 올바르지 않습니다."
            );
            return;
        }

        // 6️⃣ 관리자 조회 또는 생성
        Admin admin = adminRepository
                .findByProviderAndProviderUserId(
                        userInfo.getProvider(),
                        userInfo.getProviderUserId()
                )
                .orElseGet(() ->
                        adminRepository.save(
                                Admin.create(
                                        userInfo.getProvider(),
                                        userInfo.getProviderUserId(),
                                        userInfo.getEmail(),
                                        userInfo.getName()
                                )
                        )
                );

        // 7️⃣ 재로그인 시 프로필 동기화
        boolean changed = false;

        if (!userInfo.getEmail().equals(admin.getEmail())) {
            admin.updateProfile(userInfo.getEmail(), null);
            changed = true;
        }

        if (!userInfo.getName().equals(admin.getName())) {
            admin.updateProfile(null, userInfo.getName());
            changed = true;
        }

        if (changed) {
            adminRepository.save(admin);
        }

        // 8️⃣ JWT 발급
        String accessToken = jwtProvider.generateAccessToken(admin.getId());

        // 9️⃣ JWT를 HttpOnly Cookie로 설정
        Cookie jwtCookie = new Cookie("accessToken", accessToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60); // 1시간

        // 로컬 개발 환경에서는 false
        // 운영(HTTPS)에서는 반드시 true
        jwtCookie.setSecure(cookieSecure);

        response.addCookie(jwtCookie);

        // 🔟 프론트로 리다이렉트 (토큰 전달 x)
        response.sendRedirect(redirectUri);
    }
}
