package com.team.house.housetalk.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.house.housetalk.building.entity.BuildingEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "admin",
        uniqueConstraints = {
                // 같은 OAuth 제공자에서 같은 사용자(sub)가 중복 저장되지 않도록 보장
                @UniqueConstraint(name = "uk_admin_provider_user", columnNames = {"provider", "provider_user_id"}),
                // 이메일은 변경될 수도 있지만, 운영 정책상 유니크로 관리하고 싶다면 유지
                @UniqueConstraint(name = "uk_admin_email", columnNames = {"email"})
        },
        indexes = {
                @Index(name = "idx_admin_provider_user", columnList = "provider, provider_user_id"),
                @Index(name = "idx_admin_email", columnList = "email")
        }
)
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * OAuth 제공자 (예: "google")
     */
    @Column(nullable = false, length = 20)
    private String provider;

    /**
     * OAuth 제공자가 내려주는 고유 사용자 식별자
     * Google 기준 "sub"
     */
    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    /**
     * 이메일 (OAuth에서 가져옴)
     */
    @Column(nullable = false, length = 320)
    private String email;

    /**
     * 표시 이름 (OAuth에서 가져옴)
     */
    @Column(nullable = false, length = 80)
    private String name;

    /**
     * Admin이 관리하는 건물 목록
     * - Building이 연관관계의 주인
     * - 조회 용도 (읽기 전용)
     */
    @JsonIgnore
    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
    private List<BuildingEntity> buildings = new ArrayList<>();

    /**
     * 최초 생성 시각 / 마지막 수정 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 서비스에서 Admin을 만들 때 사용할 팩토리 메서드
     * - OAuth 로그인 성공 시점에 생성
     */
    public static Admin create(String provider, String providerUserId, String email, String name) {
        Admin admin = new Admin();
        admin.provider = requireText(provider, "provider");
        admin.providerUserId = requireText(providerUserId, "providerUserId");
        admin.email = requireText(email, "email");
        admin.name = requireText(name, "name");
        return admin;
    }

    /**
     * OAuth 재로그인 시, 이름/이메일이 바뀌었을 수 있으므로 동기화용 메서드 제공
     * (필요한 값만 업데이트)
     */
    public void updateProfile(String email, String name) {
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
