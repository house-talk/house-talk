package com.team.house.housetalk.notice.controller;

import com.team.house.housetalk.notice.entity.NoticeImage;
import com.team.house.housetalk.notice.repository.NoticeImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices/{noticeId}/images")
public class NoticeImageController {

    private final NoticeImageRepository noticeImageRepository;

    /**
     * 공지 이미지 목록 조회 (관리자 / 세입자 공용)
     */
    @GetMapping
    public List<NoticeImageResponse> getImages(
            @PathVariable Long noticeId
    ) {
        return noticeImageRepository.findByNoticeId(noticeId)
                .stream()
                .map(NoticeImageResponse::from)
                .toList();
    }

    /**
     * 🗑 공지 이미지 삭제 (관리자만)
     */
    @DeleteMapping("/{imageId}")
    public void deleteImage(
            @PathVariable Long noticeId,
            @PathVariable Long imageId,
            Authentication authentication
    ) {
        // 관리자 인증
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new IllegalStateException("관리자만 접근할 수 있습니다.");
        }

        NoticeImage image = noticeImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));

        // 공지 소속 검증
        if (!image.getNotice().getId().equals(noticeId)) {
            throw new IllegalStateException("공지 정보가 일치하지 않습니다.");
        }

        noticeImageRepository.delete(image);
    }

    /**
     * 📦 응답 DTO (컨트롤러 내부 전용)
     */
    public record NoticeImageResponse(
            Long id,
            String imageUrl,
            Integer orderIndex
    ) {
        public static NoticeImageResponse from(NoticeImage image) {
            return new NoticeImageResponse(
                    image.getId(),
                    image.getImageUrl(),
                    image.getOrderIndex()
            );
        }
    }
}
