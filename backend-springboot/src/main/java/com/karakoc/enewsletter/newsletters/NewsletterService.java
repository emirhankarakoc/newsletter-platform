package com.karakoc.enewsletter.newsletters;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface NewsletterService {
    Newsletter createProject(String ownerUserId, CreateNewsletterRequest r) throws IOException;
    Newsletter updateNewsletterBasics(String loggedUserId, String newsletterId, UpdateNewsletterNameAndDescriptionRequest r) throws IOException;
    Newsletter updateNewsletterImage(String loggedUserId, String newsletterId, UpdateNewsletterImageRequest r) throws IOException;
    List<NewsletterManager.NewsletterResponse> getMyProjects(String ownerUserId);
    ResponseEntity sendMessageToSubscribers(String userId,String newsletterId,String subject,String htmlContent) throws IOException;
    NewsletterManager.NewsletterResponse getNewsletterById(String id);
}
