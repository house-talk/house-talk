package com.team.house.housetalk.tenant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tenant")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tenantCode;

    /* ⭐ 인증용 이름 */
    @Column(nullable = false)
    private String name;

    /* ⭐ 비밀번호 해시 */
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TenantBuilding> tenantBuildings = new ArrayList<>();

    /* =========================
       생성자 (팩토리)
    ========================= */

    private Tenant(String name, String phoneNumber,String passwordHash, String tenantCode) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.tenantCode = tenantCode;
        this.phoneNumber = phoneNumber;
    }

    public static Tenant create(String name,String phoneNumber, String passwordHash, String tenantCode) {
        return new Tenant(name, phoneNumber,passwordHash, tenantCode);
    }

    /* =========================
       JPA 생명주기
    ========================= */

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

