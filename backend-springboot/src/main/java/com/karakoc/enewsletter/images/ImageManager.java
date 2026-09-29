package com.karakoc.enewsletter.images;


import com.karakoc.enewsletter.cloudflare.R2Service;
import com.karakoc.enewsletter.exceptions.general.NotfoundException;
import com.karakoc.enewsletter.user.User;
import com.karakoc.enewsletter.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ImageManager implements ImageService {
    private final ImageRepository imageRepository;
    private final R2Service r2Service;
    private final UserRepository userRepository;

    @Override
    public ImageController.ImageRecord uploadImage(String ownerId, MultipartFile file) {
        User imageUploaderUser = userRepository.findById(ownerId).orElseThrow(() -> new NotfoundException("User not found."));
        String fileKey = r2Service.uploadFile(file);
        Image image = new Image();
        image.setId(UUID.randomUUID().toString());
        image.setUserId(imageUploaderUser.getId());
        image.setCloudinaryKey(fileKey);
        imageRepository.save(image);
        return imageToRecord(image);
    }

    @Override
    public ImageController.ImageRecord getImage(String ownerId, String imageId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotfoundException("User not found."));
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new NotfoundException("Image not found."));
        return imageToRecord(image);

    }

    @Override
    public List<ImageController.ImageRecord> getAllImagesByUserId(String ownerId) {
        // Kullanıcının varlığını kontrol ediyoruz.
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotfoundException("User not found."));

        // Kullanıcıya ait tüm resimleri alıyoruz.
        List<Image> images = imageRepository.findByUserId(ownerId);

        // Her resmi ImageRecord'a çevirip listeye dönüştürüyoruz.
        return images.stream()
                .map(this::imageToRecord)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteImage(String ownerId, String imageId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotfoundException("User not found."));
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new NotfoundException("Image not found."));
        r2Service.destroy(image.getCloudinaryKey());
        imageRepository.delete(image);
    }

    private ImageController.ImageRecord imageToRecord(Image image) {
        return new ImageController.ImageRecord(image.getId(), image.getUserId(), r2Service.getPublicUrl(image.getCloudinaryKey()));
    }

}

