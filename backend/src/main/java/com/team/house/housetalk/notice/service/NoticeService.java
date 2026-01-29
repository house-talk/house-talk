package com.team.house.housetalk.notice.service;

import com.team.house.housetalk.admin.entity.Admin;
import com.team.house.housetalk.admin.repository.AdminRepository;
import com.team.house.housetalk.building.entity.BuildingEntity;
import com.team.house.housetalk.building.repository.BuildingRepository;
import com.team.house.housetalk.notice.dto.NoticeCreateRequest;
import com.team.house.housetalk.notice.dto.NoticeResponse;
import com.team.house.housetalk.notice.dto.NoticeUpdateRequest;
import com.team.house.housetalk.notice.dto.NoticeListResponse;
import com.team.house.housetalk.notice.entity.Notice;
import com.team.house.housetalk.notice.entity.NoticeImage;
import com.team.house.housetalk.notice.repository.NoticeImageRepository;
import com.team.house.housetalk.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static final String UPLOAD_DIR =
            System.getProperty("user.home") + "/house-talk/uploads/notices";

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

    /**
     * 공지 목록 조회 (기존 - 유지)
     */
    @Transactional(readOnly = true)
    public List<NoticeResponse> getNotices(Long buildingId) {
        return noticeRepository.findByBuildingIdOrderByCreatedAtDesc(buildingId)
                .stream()
                .map(NoticeResponse::from)
                .toList();
    }

    /**
     * ⭐ 공지 검색 + 페이징 조회 (신규)
     */
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

        Page<Notice> result;

        if (keyword == null || keyword.isBlank()) {
            result = noticeRepository.findByBuildingId(buildingId, pageable);
        } else {
            result = noticeRepository.search(buildingId, keyword, pageable);
        }

        return result.map(NoticeListResponse::from);
    }

    /**
     * 공지 단건 조회
     */
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long buildingId, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다."));

        if (!notice.getBuilding().getId().equals(buildingId)) {
            throw new IllegalStateException("건물 정보가 일치하지 않습니다.");
        }

        // LAZY 초기화
        notice.getImages().size();

        return NoticeResponse.from(notice);
    }

    /**
     * 공지 수정
     */
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

        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            for (Long imageId : request.getDeleteImageIds()) {
                NoticeImage image = noticeImageRepository.findById(imageId)
                        .orElseThrow(() -> new IllegalArgumentException("이미지 없음"));

                if (!image.getNotice().getId().equals(notice.getId())) {
                    throw new IllegalStateException("공지 이미지 불일치");
                }

                noticeImageRepository.delete(image);
            }
        }

        saveNoticeFiles(notice, request.getFiles());
    }

    /**
     * 공지 삭제
     */
    public void deleteNotice(
            Long buildingId,
            Long noticeId,
            Long adminId
    ) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지를 찾을 수 없습니다."));

        if (!notice.getBuilding().getId().equals(buildingId)) {
            throw new IllegalStateException("건물 정보가 일치하지 않습니다.");
        }

        if (!notice.getBuilding().getAdmin().getId().equals(adminId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        noticeRepository.delete(notice);
    }

    /**
     * 파일 저장 로직
     */
    private void saveNoticeFiles(Notice notice, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        Path uploadDir = Paths.get(UPLOAD_DIR);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패", e);
        }

        int orderIndex = 0;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalName = file.getOriginalFilename();
            String ext = StringUtils.getFilenameExtension(originalName);
            String savedName = UUID.randomUUID() + "." + ext;

            Path targetPath = uploadDir.resolve(savedName);

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
