package com.team.house.housetalk.notice.repository;

import com.team.house.housetalk.notice.entity.Notice;
import com.team.house.housetalk.notice.entity.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {

    /**
     * 특정 공지에 속한 이미지 목록 조회
     */
    List<NoticeImage> findByNoticeId(Long noticeId);

    void deleteByNoticeAndImageUrlIn(Notice notice, List<String> imageUrls);
}
