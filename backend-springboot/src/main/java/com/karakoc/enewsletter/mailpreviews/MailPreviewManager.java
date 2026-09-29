package com.karakoc.enewsletter.mailpreviews;

import com.karakoc.enewsletter.exceptions.general.BadRequestException;
import com.karakoc.enewsletter.exceptions.general.ForbiddenException;
import com.karakoc.enewsletter.exceptions.general.NotfoundException;
import com.sun.mail.util.UUDecoderStream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MailPreviewManager implements MailPreviewService{
    private final MailPreviewRepository mailPreviewRepository;


    @Override
    public MailPreview getMailPreview(String mailPreviewId, String ownerId) {
        MailPreview preview = mailPreviewRepository.findById(mailPreviewId).orElseThrow(()->new NotfoundException("Mail Preview not found."));
        if (!preview.getOwnerId().equals(ownerId)){
            throw new ForbiddenException("Forbidden");
        }
        return preview;
    }

    @Override
    public List<MailPreview> getAllMyMailPreviews(String ownerId) {
         return mailPreviewRepository.findAllByOwnerId(ownerId);

    }

    @Override
    public void deleteMailPreview(String ownerId, String mailPreviewId) {
        MailPreview preview = mailPreviewRepository.findById(mailPreviewId).orElseThrow(()->new NotfoundException("Mail Preview not found."));
        if (!preview.getOwnerId().equals(ownerId)){
            throw new ForbiddenException("Forbidden");
        }
        mailPreviewRepository.delete(preview);
    }

//    @Override
//    public MailPreview createMailPreview(String ownerId, CreateMailPreviewRequest r) {
//        MailPreview pr = new MailPreview();
//        pr.setId(UUID.randomUUID().toString());
//        pr.setUpdatedDate(LocalDateTime.now());
//        pr.setCreatedDate(LocalDateTime.now());
//        pr.setName(r.getName());
//        pr.setOwnerId(ownerId);
//        try {
//            // HTML içeriğini byte[] olarak saklıyoruz
//            pr.setHtmlContent(r.getFile().getBytes());
//        } catch (IOException e) {
//            throw new BadRequestException("Error while reading the file");
//        }
//        return mailPreviewRepository.save(pr);
//    }


    @Override
    public MailPreview updateMailPreview(String ownerId, UpdateMailPreviewRequest r) {
        return null;
    }
}
