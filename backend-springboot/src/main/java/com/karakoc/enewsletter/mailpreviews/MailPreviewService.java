package com.karakoc.enewsletter.mailpreviews;

import java.util.List;

public interface MailPreviewService {
//    MailPreview createMailPreview(String ownerId,CreateMailPreviewRequest r);
    MailPreview getMailPreview(String ownerId,String mailPreviewId);
    List<MailPreview> getAllMyMailPreviews(String ownerId);
    void deleteMailPreview(String ownerId,String mailPreviewId);
    MailPreview updateMailPreview(String ownerId,UpdateMailPreviewRequest r);

}
