package com.team.house.housetalk.invite.repository;

import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.invite.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    /**
     * 특정 건물의 현재 활성화된 초대코드 조회
     */
    Optional<Invite> findByBuildingAndIsActiveTrue(BuildingEntity building);

    /**
     * 초대코드 문자열로 활성화된 초대코드 조회 (세입자 검증용)
     */
    Optional<Invite> findByInviteCodeAndIsActiveTrue(String inviteCode);
}
