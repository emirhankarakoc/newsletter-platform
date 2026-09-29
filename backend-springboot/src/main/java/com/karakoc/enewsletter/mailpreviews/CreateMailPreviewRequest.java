package com.karakoc.enewsletter.mailpreviews;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class CreateMailPreviewRequest {
    private String name;
    private MultipartFile file;
}
