package com.karakoc.enewsletter.images;

import com.karakoc.enewsletter.exceptions.general.BadRequestException;
import com.karakoc.enewsletter.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/images")
@AllArgsConstructor
public class ImageController {
    private final ImageService imageService;

    public record ImageRecord(String id, String userId, String imageUrl) {}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ImageRecord postImage(@AuthenticationPrincipal UserPrincipal principal, @ModelAttribute MultipartFile file) {
        if (file.isEmpty()){
            throw new BadRequestException("File can not be empty.");
        }
        return imageService.uploadImage(principal.getUserId(), file);
    }

    @GetMapping(value = "/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ImageRecord getImage(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String imageId) {
        return imageService.getImage(principal.getUserId(), imageId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ImageRecord> getAllImages(@AuthenticationPrincipal UserPrincipal principal) {
        return imageService.getAllImagesByUserId(principal.getUserId());
    }

    @DeleteMapping("/{imageId}")
    public void deleteImage(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String imageId) {
        imageService.deleteImage(principal.getUserId(), imageId);
    }
}
