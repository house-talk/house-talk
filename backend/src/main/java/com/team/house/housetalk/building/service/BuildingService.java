package com.team.house.housetalk.building.service;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final AdminRepository adminRepository;

    /**
     * ⭐ adminId(PK)로 관리자 조회 (JWT 인증용)
     */
    public Admin getAdminById(Long adminId) {
        if (adminId == null) {
            throw new IllegalArgumentException("관리자 ID가 없습니다");
        }

        return adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다"));
    }

    /**
     * 이메일로 관리자 조회 (OAuth 인증용 - 유지)
     */
    public Admin getAdminByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일 정보가 없습니다");
        }

        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다"));
    }

    /**
     * 특정 관리자가 관리하는 건물 목록 조회
     */
    public List<BuildingEntity> getBuildingsByAdmin(Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }

        return buildingRepository.findByAdmin(admin);
    }

    /**
     * 건물 생성
     */
    public BuildingEntity createBuilding(BuildingEntity building) {
        if (building == null) {
            throw new IllegalArgumentException("건물 정보가 없습니다");
        }

        validateBuilding(building);

        return buildingRepository.save(building);
    }

    /**
     * 건물 수정
     */
    public BuildingEntity updateBuilding(
            Long buildingId,
            Admin admin,
            String name,
            String address,
            Integer totalFloors,
            Integer totalUnits
    ) {
        if (buildingId == null) {
            throw new IllegalArgumentException("건물 ID가 없습니다");
        }

        if (admin == null) {
            throw new IllegalArgumentException("관리자 정보가 없습니다");
        }

        BuildingEntity building = buildingRepository.findByIdAndAdmin(buildingId, admin)
                .orElseThrow(() -> new IllegalArgumentException("수정할 건물을 찾을 수 없습니다"));

        // 엔티티 내부 로직으로 상태 변경 (setter 제거)
        building.update(
                name,
                address,
                totalFloors,
                totalUnits
        );

        return buildingRepository.save(building);
    }


    /**
     * 건물 삭제
     */
    public void deleteBuilding(Long buildingId, Admin admin) {
        if (buildingId == null) {
            throw new IllegalArgumentException("건물 ID가 없습니다");
        }

        if (admin == null) {
            throw new IllegalArgumentException("관리자 정보가 없습니다");
        }

        BuildingEntity building = buildingRepository.findByIdAndAdmin(buildingId, admin)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 건물을 찾을 수 없습니다"));

        buildingRepository.delete(building);
    }

    /**
     * 조회 + 권한 검증(buildingController에서 쓰임)
     */
    public BuildingEntity getBuildingById(Long buildingId, Admin admin) {
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("건물 없음"));

        if (!building.getAdmin().equals(admin)) {
            throw new AccessDeniedException("접근 권한 없음");
        }

        return building;
    }

    /**
     * 조회 전용(UnitController에서 쓰임)
     */
    public BuildingEntity getBuildingById(Long buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("건물 없음"));
    }



    /**
     * 공통 검증 로직 (기존 create 로직에서 분리)
     */
    private void validateBuilding(BuildingEntity building) {
        if (building.getAdmin() == null) {
            throw new IllegalArgumentException("관리자 정보가 없습니다");
        }

        if (building.getName() == null || building.getName().isBlank()) {
            throw new IllegalArgumentException("건물 이름은 필수입니다");
        }

        if (building.getAddress() == null || building.getAddress().isBlank()) {
            throw new IllegalArgumentException("건물 주소는 필수입니다");
        }

        if (building.getTotalFloors() != null && building.getTotalFloors() <= 0) {
            throw new IllegalArgumentException("총 층수는 1 이상이어야 합니다");
        }

        if (building.getTotalUnits() != null && building.getTotalUnits() <= 0) {
            throw new IllegalArgumentException("총 세대 수는 1 이상이어야 합니다");
        }
    }
}
