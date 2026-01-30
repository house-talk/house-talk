package com.team.house.housetalk.notice.repository;

import com.team.house.housetalk.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 기존 전체 조회 (유지)
    List<Notice> findByBuildingIdOrderByCreatedAtDesc(Long buildingId);

    // ⭐ 페이징 + 최신순
    Page<Notice> findByBuildingId(
            Long buildingId,
            Pageable pageable
    );

    // ⭐ 검색 + 페이징 (제목 + 내용)
    @Query("""
        SELECT n
        FROM Notice n
        WHERE n.building.id = :buildingId
          AND (
            n.title LIKE %:keyword%
            OR n.content LIKE %:keyword%
          )
    """)
    Page<Notice> search(
            @Param("buildingId") Long buildingId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
