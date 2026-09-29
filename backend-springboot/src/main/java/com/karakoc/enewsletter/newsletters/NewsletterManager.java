package com.karakoc.enewsletter.newsletters;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.karakoc.enewsletter.cloudflare.R2Service;
import com.karakoc.enewsletter.customers.Customer;
import com.karakoc.enewsletter.exceptions.general.BadRequestException;
import com.karakoc.enewsletter.exceptions.general.ForbiddenException;
import com.karakoc.enewsletter.exceptions.general.InternalServerErrorException;
import com.karakoc.enewsletter.exceptions.general.NotfoundException;
import com.karakoc.enewsletter.gmail.googletoken.GoogleToken;
import com.karakoc.enewsletter.gmail.googletoken.GoogleTokenRepository;
import com.karakoc.enewsletter.gmail.mail.GmailMailService;
import com.karakoc.enewsletter.user.User;
import com.karakoc.enewsletter.user.UserRepository;
import com.karakoc.enewsletter.user.UserStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class NewsletterManager implements NewsletterService {
    private final NewsletterRepository newsletterRepository;
    private final UserRepository userRepository;
    private final GmailMailService gmailMailService;
    private final R2Service r2Service;
    private final GoogleTokenRepository googleTokenRepository;

    @Override
    @Transactional
    public Newsletter createProject(String ownerUserId, CreateNewsletterRequest r) {
        Newsletter newsletter = new Newsletter();

        try {
            if (r.getFile().isEmpty()) {
                throw new BadRequestException("File is missing or empty.");
            }
            String fileKey = r2Service.uploadFile(r.getFile());

            newsletter.setId(UUID.randomUUID().toString());
            newsletter.setName(r.getName());
            newsletter.setImageKey(fileKey);
            newsletter.setDescription(r.getDescription());
            newsletter.setCustomers(new ArrayList<>());
            newsletter.setOwnerUserId(ownerUserId);
            newsletterRepository.save(newsletter);


        } catch (Exception e) {
            throw new BadRequestException("File upload failed: " + e.getMessage());
        }
        return newsletter;


    }

    @Override
    public Newsletter updateNewsletterBasics(String loggedUserId, String newsletterId, UpdateNewsletterNameAndDescriptionRequest r) throws IOException {
        Newsletter newsletter = newsletterRepository.findById(newsletterId).orElseThrow(() -> new NotfoundException("Newsletter not found."));
        if (!newsletter.getOwnerUserId().equals(loggedUserId)) {
            throw new ForbiddenException("Forbidden");
        }
        newsletter.setName(r.getName());
        newsletter.setDescription(r.getDescription());
        newsletterRepository.save(newsletter);
        return newsletter;
    }

    @Transactional
    public Newsletter updateNewsletterImage(String loggedUserId, String newsletterId, UpdateNewsletterImageRequest r) throws IOException {
        Newsletter newsletter = newsletterRepository.findById(newsletterId).orElseThrow(() -> new NotfoundException("Newsletter not found."));
        if (!newsletter.getOwnerUserId().equals(loggedUserId)) {
            throw new ForbiddenException("Forbidden");
        }
        try {
            if (r.getFile().isEmpty()) {
                throw new BadRequestException("File is missing or empty.");
            }
            String oldFileKey = newsletter.getImageKey();
            String fileKey = r2Service.uploadFile(r.getFile());
            newsletter.setImageKey(fileKey);
            newsletterRepository.save(newsletter);
            r2Service.destroy(oldFileKey);

        } catch (Exception e) {
            log.error("Cloudinary upload failed", e);
            throw new BadRequestException("File update failed: " + e.getMessage());
        }
        return ResponseEntity.ok(newsletter).getBody();

    }


    @Override
    public List<NewsletterResponse> getMyProjects(String ownerUserId) {
        List<Newsletter> newsletters = newsletterRepository.findAllByOwnerUserId(ownerUserId);
        return newsletters.stream()
                .map(newsletter -> new NewsletterResponse(
                        newsletter.getId(),
                        newsletter.getName(),
                        newsletter.getDescription(),
                        r2Service.getPublicUrl(newsletter.getImageKey()),
                        newsletter.getCustomers()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public ResponseEntity sendMessageToSubscribers(String userId, String newsletterId, String subject2, String htmlContent2) {
        Newsletter newsletter = validateNewsletter(newsletterId);
        User user = validateUser(userId);
        validateUserAccessToken(user);
        GoogleToken gt = googleTokenRepository.findById(user.getGoogleTokenId()).orElseThrow(() -> new NotfoundException("We can't find your google token right now, try log-out and log-in same google account or contact us."));

        int sentMailCounter = 0;
        List<String> failedEmails = new ArrayList<>();

        if (newsletter.getOwnerUserId().equals(user.getId())) {
            List<Customer> subscribers = newsletter.getCustomers();

            for (Customer customer : subscribers) {
                try {
                    gmailMailService.sendMail(gt, customer.getEmail(), subject2, htmlContent2,newsletterId,customer);
                            sentMailCounter++;
                } catch (Exception e) {
                    // Eğer doğrudan veya cause olarak GoogleJsonResponseException varsa
                    if (e.getCause() != null && e.getCause() instanceof GoogleJsonResponseException) {
                        throw new InternalServerErrorException("Oauth2 Token expired, log out and re-login please:");
                    }
                    failedEmails.add(customer.getEmail());
                }


            }
        } else {
            throw new ForbiddenException("Forbidden.");
        }

        SendMailResponse response = new SendMailResponse();
        response.setSuccessCount(sentMailCounter);
        response.setFailedEmails(failedEmails);

        return ResponseEntity.ok(response);
    }

    private void validateUserAccessToken(User user) {

        if (user.getUserStatus() == UserStatus.GOOGLE_NOT_VERIFICATED) {
            Optional<GoogleToken> gt = googleTokenRepository.findById(user.getGoogleTokenId());
            if (gt.isEmpty()) {
                throw new BadRequestException("You must connect your Gmail account.");
            }
            else{
                throw new InternalServerErrorException("Sorry buddy , there is a problem with your google account. we cant reach right now and this is unexpected exception. try log-out and log-in to your google account.");
            }

        }
    }

    private Newsletter validateNewsletter(String newsletterId) {
        return newsletterRepository.findById(newsletterId)
                .orElseThrow(() -> new NotfoundException("Newsletter not found."));
    }

    private User validateUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotfoundException("User not found."));
    }

    public record NewsletterResponse(String id, String name, String description, String imageUrl,
                                     List<Customer> customers) {
    }

    @Override
    public NewsletterResponse getNewsletterById(String id) {
        Newsletter newsletter = newsletterRepository.findById(id).orElseThrow(() -> new NotfoundException("Newsletter not found."));
        // Newsletter nesnesini NewsletterResponse'a dönüştür
        return new NewsletterResponse(
                newsletter.getId(),
                newsletter.getName(),
                newsletter.getDescription(),
                r2Service.getPublicUrl(newsletter.getImageKey()),
                newsletter.getCustomers()
        );
    }
}
