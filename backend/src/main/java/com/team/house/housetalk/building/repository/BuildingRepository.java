package com.team.house.housetalk.building.repository;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.building.entity.BuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<BuildingEntity, Long> {

    /**
     * 특정 관리자가 관리하는 건물 목록 조회
     */
    List<BuildingEntity> findByAdmin(Admin admin);

    /**
     * ⭐ 건물 단건 조회 + 관리자 소유 검증 (수정/삭제용)
     */
    Optional<BuildingEntity> findByIdAndAdmin(Long id, Admin admin);
}
