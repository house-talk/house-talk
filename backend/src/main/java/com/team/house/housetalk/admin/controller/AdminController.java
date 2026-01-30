package com.team.house.housetalk.admin.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminRepository adminRepository;

    @GetMapping("/me")
    public AdminMeResponse me(Authentication authentication) {

        // 🔥 JWT 인증 기준
        // JwtAuthenticationFilter에서 principal = adminId(Long) 로 넣어줌
        Long adminId = (Long) authentication.getPrincipal();

        // 관리자 조회
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() ->
                        new IllegalStateException("로그인된 관리자 정보를 찾을 수 없습니다.")
                );

        // 응답 DTO 반환
        return AdminMeResponse.from(admin);
    }

    /**
     * 관리자 본인 정보 응답 DTO
     * (엔티티 직접 노출 ❌)
     */
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
