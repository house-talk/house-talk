package com.team.house.housetalk.invite.service;

import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.invite.entity.Invite;
import com.team.house.housetalk.invite.repository.InviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class InviteService {

    private final InviteRepository inviteRepository;

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final int GROUP_SIZE = 4;

    /**
     * 관리자용 초대코드 발급
     * - 기존 활성 코드가 있으면 무효화
     * - 새 코드 생성 후 저장
     */
    public Invite createInvite(BuildingEntity building) {

        // 1️⃣ 기존 활성 코드 무효화
        inviteRepository.findByBuildingAndIsActiveTrue(building)
                .ifPresent(Invite::deactivate);

        // 2️⃣ 새 초대코드 생성
        String inviteCode = generateInviteCode();

        // 3️⃣ 저장
        Invite invite = Invite.create(building, inviteCode);
        return inviteRepository.save(invite);
    }

    /**
     * 🔥 관리자용 초대코드 조회 (유지 핵심)
     * - 건물 기준 현재 활성화된 초대코드 조회
     * - 없으면 null 반환
     */
    @Transactional(readOnly = true)
    public Invite findByBuilding(BuildingEntity building) {
        return inviteRepository.findByBuildingAndIsActiveTrue(building)
                .orElse(null);
    }

    /**
     * 세입자용 초대코드 검증
     */
    @Transactional(readOnly = true)
    public Invite validateInvite(String inviteCode) {
        return inviteRepository.findByInviteCodeAndIsActiveTrue(inviteCode)
                .orElseThrow(() ->
                        new IllegalArgumentException("유효하지 않은 초대코드입니다.")
                );
    }

    /**
     * 초대코드 생성 규칙
     * 예: A9F3-K2LQ
     */
    private String generateInviteCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder raw = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            raw.append(CODE_CHARS.charAt(
                    random.nextInt(CODE_CHARS.length()))
            );
        }

        // 4-4 형식으로 하이픈 추가
        return raw.substring(0, GROUP_SIZE) + "-" + raw.substring(GROUP_SIZE);
    }
}
