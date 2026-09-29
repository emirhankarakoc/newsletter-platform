package com.karakoc.enewsletter.mailpreviews;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class MailPreview {
    @Id
    private String id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
//    private byte[] htmlContent;  // BLOB olarak tutmak için byte[] kullanıyoruz
    private String ownerId;

}
