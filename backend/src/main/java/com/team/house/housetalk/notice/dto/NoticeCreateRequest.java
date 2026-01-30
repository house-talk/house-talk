package com.team.house.housetalk.notice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class NoticeCreateRequest {

    private String title;
    private String content;

    // ✅ 파일 필드 추가
    private List<MultipartFile> files;
}
