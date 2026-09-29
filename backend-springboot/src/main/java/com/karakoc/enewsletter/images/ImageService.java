package com.karakoc.enewsletter.images;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    ImageController.ImageRecord uploadImage(String ownerId, MultipartFile file);
    ImageController.ImageRecord getImage(String ownerId, String imageId);
    List<ImageController.ImageRecord> getAllImagesByUserId(String ownerId);
    void deleteImage(String ownerId, String imageId);
}
