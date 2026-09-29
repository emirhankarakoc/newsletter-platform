package com.karakoc.enewsletter.mailpreviews;

import com.karakoc.enewsletter.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mailpreviews")
@AllArgsConstructor
public class MailPreviewController {
    private final MailPreviewService mailPreviewService;
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public MailPreview postMailPreview(@AuthenticationPrincipal UserPrincipal principal, @ModelAttribute CreateMailPreviewRequest r){
//        return mailPreviewService.createMailPreview(principal.getUserId(), r);
//    }
    @GetMapping("/my")
    public List<MailPreview> getAllMyMailPreviews(@AuthenticationPrincipal UserPrincipal principal){
        return mailPreviewService.getAllMyMailPreviews(principal.getUserId());
    }

}
