package com.karakoc.enewsletter.newsletters;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateNewsletterImageRequest {
    private MultipartFile file;
}
