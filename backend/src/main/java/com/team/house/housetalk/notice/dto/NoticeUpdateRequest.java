package com.team.house.housetalk.notice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class NoticeUpdateRequest {

    private String title;
    private String content;

    // 🔥 삭제할 기존 이미지 ID
    private List<Long> deleteImageIds;

    // 🔥 새로 추가할 파일들
    private List<MultipartFile> files;
}

