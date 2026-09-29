package com.karakoc.enewsletter.gmail.googletoken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class GoogleToken {

    @Id
    private String id; // UUID

    //baglanilmis email adresi
    private String oauth2EmailAddress;

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    private Instant expiresAt;

}
