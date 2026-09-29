package com.karakoc.enewsletter.newsletters;


import lombok.Data;

@Data
public class UpdateNewsletterNameAndDescriptionRequest {
    private String name;
    private String description;
}
