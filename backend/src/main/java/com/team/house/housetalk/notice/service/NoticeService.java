package com.team.house.housetalk.notice.service;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.repository.BuildingRepository;
import com.team.house.housetalk.notice.dto.*;
import com.team.house.housetalk.notice.entity.Notice;
import com.team.house.housetalk.notice.entity.NoticeImage;
import com.team.house.housetalk.notice.repository.NoticeImageRepository;
import com.team.house.housetalk.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final BuildingRepository buildingRepository;
    private final AdminRepository adminRepository;

    // ✅ 여기 핵심: 설정에서 주입
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 공지 생성
     */
    public Long createNotice(
            Long buildingId,
            Long adminId,
            NoticeCreateRequest request
    ) {
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("건물을 찾을 수 없습니다."));

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        if (!building.getAdmin().getId().equals(adminId)) {
            throw new IllegalStateException("해당 건물의 관리자가 아닙니다.");
        }

        Notice notice = Notice.builder()
                .building(building)
                .admin(admin)
                .title(request.getTitle())
                .content(request.getContent())
                .writer("관리자")
                .build();

        noticeRepository.save(notice);
        saveNoticeFiles(notice, request.getFiles());

        return notice.getId();
    }

    @Transactional(readOnly = true)
    public List<NoticeResponse> getNotices(Long buildingId) {
        return noticeRepository.findByBuildingIdOrderByCreatedAtDesc(buildingId)
                .stream()
                .map(NoticeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<NoticeListResponse> searchNotices(
            Long buildingId,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Notice> result = (keyword == null || keyword.isBlank())
                ? noticeRepository.findByBuildingId(buildingId, pageable)
                : noticeRepository.search(buildingId, keyword, pageable);

        return result.map(NoticeListResponse::from);
    }

    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long buildingId, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다."));

        if (!notice.getBuilding().getId().equals(buildingId)) {
            throw new IllegalStateException("건물 정보가 일치하지 않습니다.");
        }

        notice.getImages().size(); // LAZY 초기화
        return NoticeResponse.from(notice);
    }

    public void updateNotice(
            Long buildingId,
            Long noticeId,
            Long adminId,
            NoticeUpdateRequest request
    ) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지 없음"));

        if (!notice.getBuilding().getAdmin().getId().equals(adminId)) {
            throw new IllegalStateException("권한 없음");
        }

        notice.update(request.getTitle(), request.getContent());

        if (request.getDeleteImageIds() != null) {
            for (Long imageId : request.getDeleteImageIds()) {
                NoticeImage image = noticeImageRepository.findById(imageId)
                        .orElseThrow(() -> new IllegalArgumentException("이미지 없음"));
                noticeImageRepository.delete(image);
            }
        }

        saveNoticeFiles(notice, request.getFiles());
    }

    public void deleteNotice(
            Long buildingId,
            Long noticeId,
            Long adminId
    ) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다."));

        if (!notice.getBuilding().getAdmin().getId().equals(adminId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        noticeRepository.delete(notice);
    }

    /**
     * ✅ 파일 저장 (핵심 수정 부분)
     */
    private void saveNoticeFiles(Notice notice, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        // 🔥 /data/uploads/notices (prod)
        // 🔥 ./uploads/notices (local)
        Path noticeDir = Paths.get(uploadDir, "notices");

        try {
            Files.createDirectories(noticeDir);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패", e);
        }

        int orderIndex = 0;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalName = file.getOriginalFilename();
            String ext = StringUtils.getFilenameExtension(originalName);
            String savedName = UUID.randomUUID() + "." + ext;

            Path targetPath = noticeDir.resolve(savedName);

            try {
                file.transferTo(targetPath.toFile());
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패", e);
            }

            NoticeImage noticeImage = NoticeImage.builder()
                    .notice(notice)
                    .imageUrl("/uploads/notices/" + savedName)
                    .originalName(originalName)
                    .orderIndex(orderIndex++)
                    .build();

            noticeImageRepository.save(noticeImage);
        }
    }
}
