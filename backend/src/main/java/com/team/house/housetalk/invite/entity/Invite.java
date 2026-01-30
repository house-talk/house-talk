package com.team.house.housetalk.invite.entity;

import com.team.house.housetalk.building.entity.BuildingEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "invite",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "invite_code")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 초대코드가 속한 건물
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private BuildingEntity building;

    /**
     * 초대코드 문자열 (예: A9F3-K2LQ)
     */
    @Column(name = "invite_code", nullable = false, length = 20)
    private String inviteCode;

    /**
     * 현재 활성화 여부
     * - building 당 true는 1개만 유지
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * 생성 시각
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 시각
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ===============================
       생성자 (팩토리 성격)
    =============================== */

    public static Invite create(BuildingEntity building, String inviteCode) {
        Invite invite = new Invite();
        invite.building = building;
        invite.inviteCode = inviteCode;
        invite.isActive = true;
        invite.createdAt = LocalDateTime.now();
        invite.updatedAt = LocalDateTime.now();
        return invite;
    }

    /* ===============================
       상태 변경 메서드
    =============================== */

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
}
