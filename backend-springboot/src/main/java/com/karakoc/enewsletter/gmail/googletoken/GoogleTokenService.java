package com.karakoc.enewsletter.gmail.googletoken;



import java.time.Instant;
import java.util.Optional;

public interface GoogleTokenService {
    void save(GoogleToken token);
    Optional<GoogleToken> getByEmail(String email);
    void deleteGoogleAccount(String userId);
}
