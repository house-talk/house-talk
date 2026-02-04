package com.team.house.housetalk.admin.controller;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그 확인용
import org.springframework.http.ResponseEntity; // 추가
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j // 로그 출력 기능 추가
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminRepository adminRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        // 1. 인증 객체 확인
        if (authentication == null) {
            log.error("Authentication object is null");
            return ResponseEntity.status(401).body("인증 정보가 없습니다.");
        }

        // 2. Principal 타입 및 값 확인
        Object principal = authentication.getPrincipal();
        log.info("Principal Type: {}", principal.getClass().getName());
        log.info("Principal Value: {}", principal);

        Long adminId;
        try {
            adminId = (Long) principal;
        } catch (ClassCastException e) {
            log.error("Casting failed", e);
            return ResponseEntity.status(500).body("ID 타입 변환 실패: " + principal.getClass().getName());
        }

        // 3. 관리자 조회 (여기가 가장 유력한 실패 지점)
        Admin admin = adminRepository.findById(adminId).orElse(null);

        if (admin == null) {
            log.error("Admin not found with id: {}", adminId);
            // 토큰은 있는데 DB에 유저가 없는 유령 상태 -> 재로그인 유도
            return ResponseEntity.status(404).body("회원 정보를 찾을 수 없습니다. (ID: " + adminId + ")");
        }

        // 4. 응답 반환
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
