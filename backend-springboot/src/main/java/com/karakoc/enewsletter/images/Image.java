package com.karakoc.enewsletter.images;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Image {
    @Id
    private String id;
    private String cloudinaryKey;
    private String userId;
}
