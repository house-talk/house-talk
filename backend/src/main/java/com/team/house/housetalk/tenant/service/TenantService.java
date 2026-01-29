package com.team.house.housetalk.tenant.service;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.repository.BuildingRepository;
import com.team.house.housetalk.invite.entity.Invite;
import com.team.house.housetalk.invite.repository.InviteRepository;
import com.team.house.housetalk.tenant.entity.Tenant;
import com.team.house.housetalk.tenant.entity.TenantBuilding;
import com.team.house.housetalk.tenant.repository.TenantBuildingRepository;
import com.team.house.housetalk.tenant.repository.TenantRepository;
import com.team.house.housetalk.unit.entity.Unit;
import com.team.house.housetalk.unit.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantBuildingRepository tenantBuildingRepository;
    private final InviteRepository inviteRepository;
    private final BuildingRepository buildingRepository;
    private final UnitRepository unitRepository;

    // ✅ 관리자 소유 검증용
    private final AdminRepository adminRepository;

    // ⭐ 인증용
    private final PasswordEncoder passwordEncoder;

    /* ==================================================
       ⭐ 0️⃣ 세입자 인증 (기존 / 신규 공통)
       - phoneNumber + password 기준
    ================================================== */
    public Tenant authenticateOrCreate(
            String phoneNumber,
            String password,
            String name,     // 신규일 때만 사용
            boolean newUser    // ⭐ 프론트에서 전달된 의도
    ) {

        return tenantRepository.findByPhoneNumber(phoneNumber)
                .map(existingTenant -> {

                    // ❌ 신규인데 이미 존재하는 전화번호
                    if (newUser) {
                        throw new IllegalStateException("이미 가입된 전화번호입니다.");
                    }

                    // 🔐 비밀번호 검증 (기존 이용)
                    if (!passwordEncoder.matches(password, existingTenant.getPasswordHash())) {
                        throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
                    }

                    return existingTenant;
                })
                .orElseGet(() -> {

                    // ❌ 기존 이용인데 존재하지 않는 전화번호
                    if (!newUser) {
                        throw new IllegalStateException("존재하지 않는 세입자입니다.");
                    }

                    // 🆕 신규 세입자 생성
                    Tenant tenant = Tenant.create(
                            name,
                            phoneNumber,
                            passwordEncoder.encode(password),
                            generateTenantCode()
                    );

                    return tenantRepository.save(tenant);
                });
    }

    private String generateTenantCode() {
        return UUID.randomUUID().toString();
    }


    /* ==================================================
       1️⃣ 세입자 집 추가 요청 (초대코드 기반)
       - 인증된 tenantCode 필수
    ================================================== */
    public void requestJoinBuilding(
            String inviteCode,
            String name,
            String phoneNumber,
            String unitNumber,
            String tenantCode
    ) {
        Invite invite = inviteRepository.findByInviteCodeAndIsActiveTrue(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대코드입니다."));

        BuildingEntity building = invite.getBuilding();

        Unit unit = unitRepository.findByBuildingAndUnitNumber(building, unitNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 호수입니다."));

        Tenant tenant = tenantRepository.findByTenantCode(tenantCode)
                .orElseThrow(() -> new IllegalStateException("인증되지 않은 세입자입니다."));

        if (tenantBuildingRepository.existsByTenant_IdAndUnit_Id(
                tenant.getId(), unit.getId()
        )) {
            throw new IllegalStateException("이미 해당 호수에 요청이 존재합니다.");
        }

        TenantBuilding tenantBuilding = TenantBuilding.create(
                tenant,
                building,
                unit,
                name,
                phoneNumber
        );

        tenantBuildingRepository.save(tenantBuilding);
    }

    /* ==================================================
       2️⃣ 관리자 승인 대기 목록 조회
    ================================================== */
    @Transactional(readOnly = true)
    public List<TenantBuilding> getPendingRequests(Long buildingId, Long adminId) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("건물을 찾을 수 없습니다."));

        if (!building.getAdmin().getId().equals(admin.getId())) {
            throw new IllegalStateException("해당 건물에 대한 권한이 없습니다.");
        }

        return tenantBuildingRepository.findByBuildingAndApprovedFalse(building);
    }

    /* ==================================================
       3️⃣ 관리자 승인
    ================================================== */
    @Transactional
    public void approveTenant(Long tenantBuildingId, Long adminId) {

        TenantBuilding tenantBuilding = tenantBuildingRepository.findById(tenantBuildingId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        Long ownerAdminId = tenantBuilding.getBuilding().getAdmin().getId();
        if (!ownerAdminId.equals(adminId)) {
            throw new IllegalStateException("승인 권한이 없습니다.");
        }

        // 🔥 핵심: 이미 입주한 세대인지 체크
        boolean alreadyOccupied =
                tenantBuildingRepository.existsByUnitAndApprovedTrue(
                        tenantBuilding.getUnit()
                );

        if (alreadyOccupied) {
            throw new IllegalArgumentException("이미 해당 호수에 입주한 세입자가 있습니다.");
        }

        tenantBuilding.approve();
        tenantBuilding.getUnit().updateIsOccupied(true);
    }




    /* ==================================================
       4️⃣ 관리자 거절
    ================================================== */
    public void rejectTenant(Long tenantBuildingId, Long adminId) {
        TenantBuilding tenantBuilding = tenantBuildingRepository.findById(tenantBuildingId)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        Long ownerAdminId = tenantBuilding.getBuilding().getAdmin().getId();
        if (!ownerAdminId.equals(adminId)) {
            throw new IllegalStateException("거절 권한이 없습니다.");
        }

        if (tenantBuilding.isApproved()) {
            tenantBuilding.getUnit().markVacant();
        }

        tenantBuildingRepository.delete(tenantBuilding);
    }

    /* ==================================================
   5️⃣ 세입자 전용 건물 상세 조회
   - tenantBuildingId 기준
   - 본인 소유 + 승인된 건물만 조회
================================================== */
    @Transactional(readOnly = true)
    public TenantBuilding getApprovedTenantBuilding(
            Long tenantBuildingId,
            String tenantCode
    ) {
        Tenant tenant = tenantRepository.findByTenantCode(tenantCode)
                .orElseThrow(() -> new IllegalStateException("인증되지 않은 세입자입니다."));

        return tenantBuildingRepository
                .findByIdAndTenantAndApprovedTrue(tenantBuildingId, tenant)
                .orElseThrow(() -> new IllegalArgumentException("접근할 수 없는 건물입니다."));
    }

}


