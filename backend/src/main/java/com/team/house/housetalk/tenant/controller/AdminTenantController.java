package com.team.house.housetalk.tenant.controller;

import com.team.house.housetalk.tenant.dto.TenantApprovalResponse;
import com.team.house.housetalk.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/tenants")
public class AdminTenantController {

    private final TenantService tenantService;

    /**
     * 관리자 승인 대기 목록 조회
     */
    @GetMapping("/pending")
    public List<TenantApprovalResponse> getPendingTenants(
            @RequestParam Long buildingId,
            Authentication authentication
    ) {
        // ⭐ 현재 로그인한 관리자 ID
        Long adminId = (Long) authentication.getPrincipal();

        return tenantService.getPendingRequests(buildingId, adminId)
                .stream()
                .map(TenantApprovalResponse::new)
                .toList();
    }

    /**
     * 승인
     */
    @PostMapping("/{tenantBuildingId}/approve")
    public void approveTenant(
            @PathVariable Long tenantBuildingId,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        tenantService.approveTenant(tenantBuildingId, adminId);
    }

    /**
     * 거절
     */
    @DeleteMapping("/{tenantBuildingId}")
    public void rejectTenant(
            @PathVariable Long tenantBuildingId,
            Authentication authentication
    ) {
        Long adminId = (Long) authentication.getPrincipal();
        tenantService.rejectTenant(tenantBuildingId, adminId);
    }
}
