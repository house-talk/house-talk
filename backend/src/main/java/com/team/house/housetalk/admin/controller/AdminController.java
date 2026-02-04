package com.team.house.housetalk.admin.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminRepository adminRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Object principal) {

        // 1. 로그인 정보가 아예 없는 경우
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인 정보가 없습니다.");
        }

        Admin admin = null;

        // 2. [CASE A] JWT 토큰으로 인증된 경우 (principal = Long id)
        if (principal instanceof Long adminId) {
            log.info("JWT 인증 요청: ID={}", adminId);
            admin = adminRepository.findById(adminId).orElse(null);
        }
        // 3. [CASE B] 구글 로그인 세션으로 인증된 경우 (principal = OAuth2User)
        else if (principal instanceof OAuth2User oauthUser) {
            log.info("OAuth2 세션 인증 요청");
            // 구글의 'sub' (고유 ID) 가져오기
            Map<String, Object> attributes = oauthUser.getAttributes();
            String providerId = (String) attributes.get("sub");

            // DB에서 구글 ID로 관리자 찾기 (provider="google" 고정 가정)
            if (providerId != null) {
                admin = adminRepository.findByProviderAndProviderUserId("google", providerId).orElse(null);
            }
        }
        // 4. [CASE C] 알 수 없는 타입
        else {
            log.error("알 수 없는 인증 타입: {}", principal.getClass().getName());
        }

        // 5. 결과 반환
        if (admin == null) {
            return ResponseEntity.status(404).body("회원 정보를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(AdminMeResponse.from(admin));
    }

    public record AdminMeResponse(
            Long id,
            String email,
            String name
    ) {
        public static AdminMeResponse from(Admin admin) {
            return new AdminMeResponse(
                    admin.getId(),
                    admin.getEmail(),
                    admin.getName()
            );
        }
    }
}
