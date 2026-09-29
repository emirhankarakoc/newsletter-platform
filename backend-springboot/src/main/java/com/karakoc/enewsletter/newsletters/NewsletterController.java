package com.karakoc.enewsletter.newsletters;

import com.karakoc.enewsletter.customers.CustomerService;
import com.karakoc.enewsletter.exceptions.general.BadRequestException;
import com.karakoc.enewsletter.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/newsletters")
@AllArgsConstructor
public class NewsletterController {
private final NewsletterService newsletterService;
private final CustomerService customerService;

    public record SendMailRequest(String subject, MultipartFile htmlFile){}


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Newsletter postProject(@AuthenticationPrincipal UserPrincipal principal, @ModelAttribute CreateNewsletterRequest r) throws IOException {
        return newsletterService.createProject(principal.getUserId(),r);
    }
    @PutMapping("/{newsletterId}/name-and-description")
    public Newsletter updateNewsletterBasics(@AuthenticationPrincipal UserPrincipal principal,@PathVariable String newsletterId, @RequestBody UpdateNewsletterNameAndDescriptionRequest r) throws IOException{
        return newsletterService.updateNewsletterBasics(principal.getUserId(),newsletterId,r);
    }
    @PutMapping(value = "/{newsletterId}/image" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Newsletter updateNewsletterImage(@AuthenticationPrincipal UserPrincipal principal,@PathVariable String newsletterId, @ModelAttribute UpdateNewsletterImageRequest r) throws IOException{
        return newsletterService.updateNewsletterImage(principal.getUserId(),newsletterId,r);
    }

    @GetMapping("{id}")
    public NewsletterManager.NewsletterResponse getNewsletterById(@PathVariable String id){
        return newsletterService.getNewsletterById(id);
    }

    @GetMapping("/my")
    public List<NewsletterManager.NewsletterResponse> getMyProjects(@AuthenticationPrincipal UserPrincipal principal) {
        return newsletterService.getMyProjects(principal.getUserId());
    }


    @PostMapping( value = "/{id}/sendMessage",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendMessageToSubscribers(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String id, @ModelAttribute SendMailRequest r) throws IOException {
        String htmlContent;
        try {
            if (r.htmlFile().isEmpty()){
                throw new BadRequestException("Missing file");
            }
            htmlContent = new String(r.htmlFile().getBytes());


        } catch (IOException e) {
            throw new BadRequestException("HTML dosyası okunamadı: " + e.getMessage());
        }

        // Mail gönderme işlemi
        return newsletterService.sendMessageToSubscribers(principal.getUserId(), id,r.subject(),htmlContent);
    }


    }


