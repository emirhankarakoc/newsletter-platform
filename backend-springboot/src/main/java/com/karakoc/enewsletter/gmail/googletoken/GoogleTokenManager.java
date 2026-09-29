package com.karakoc.enewsletter.gmail.googletoken;

import com.karakoc.enewsletter.exceptions.general.NotfoundException;
import com.karakoc.enewsletter.user.User;
import com.karakoc.enewsletter.user.UserRepository;
import com.karakoc.enewsletter.user.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleTokenManager implements GoogleTokenService {

    private final GoogleTokenRepository repository;
    private final UserRepository userRepository;

    @Override
    public void save(GoogleToken token) {
        repository.save(token);

    }

    @Override
    public Optional<GoogleToken> getByEmail(String email) {
        return repository.findById(email);
    }

    @Override
    @Transactional
    public void deleteGoogleAccount(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new NotfoundException("User not found."));
        GoogleToken googleToken = repository.findById(user.getGoogleTokenId()).orElseThrow(()->new NotfoundException("Google Token not found. This error shouldnt be here."));
        repository.delete(googleToken);
        user.setUserStatus(UserStatus.GOOGLE_NOT_VERIFICATED);
        user.setGoogleTokenId(null);
    }
}
