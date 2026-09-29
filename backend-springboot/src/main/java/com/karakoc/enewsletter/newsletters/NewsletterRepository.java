package com.karakoc.enewsletter.newsletters;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsletterRepository extends JpaRepository<Newsletter,String > {

    List<Newsletter> findAllByOwnerUserId(String userId);
}
