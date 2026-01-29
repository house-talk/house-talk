package com.team.house.housetalk.admin.repository;

import com.team.house.housetalk.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * OAuth 제공자 + 제공자 사용자 ID 기준으로 관리자 조회
     * (Google OAuth 로그인 시 핵심 조회 메서드)
     */
    Optional<Admin> findByProviderAndProviderUserId(String provider, String providerUserId);

    /**
     * 이메일로 관리자 조회
     * (관리자 정보 확인, 중복 정책 처리 시 사용 가능)
     */
    Optional<Admin> findByEmail(String email);

    /**
     * OAuth 제공자 + 제공자 사용자 ID 존재 여부 확인
     * (필요 시 최적화용)
     */
    boolean existsByProviderAndProviderUserId(String provider, String providerUserId);
}
