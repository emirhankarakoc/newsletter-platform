package com.karakoc.enewsletter.gmail.googletoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GoogleTokenRepository extends JpaRepository<GoogleToken,String> {
}
