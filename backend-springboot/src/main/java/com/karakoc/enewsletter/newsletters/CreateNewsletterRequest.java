package com.karakoc.enewsletter.newsletters;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateNewsletterRequest {
    private String name;
    private String description;
    private MultipartFile file;
}
