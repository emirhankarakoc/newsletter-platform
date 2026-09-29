package com.karakoc.enewsletter.mailpreviews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailPreviewRepository extends JpaRepository<MailPreview,String> {
    List<MailPreview> findAllByOwnerId(String ownerId);
}
