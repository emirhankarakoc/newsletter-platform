package com.karakoc.enewsletter.user;

import com.karakoc.enewsletter.account.requests.OAuth2Response;
import com.karakoc.enewsletter.exceptions.general.BadRequestException;
import com.karakoc.enewsletter.exceptions.general.NotfoundException;
import com.karakoc.enewsletter.exceptions.strings.ExceptionMessages;
import com.karakoc.enewsletter.gmail.googletoken.GoogleToken;
import com.karakoc.enewsletter.gmail.googletoken.GoogleTokenRepository;
import com.karakoc.enewsletter.security.WebSecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.karakoc.enewsletter.user.User.userToDTO;
import static com.karakoc.enewsletter.user.User.usersToDTOS;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserManager implements UserService{

    private final UserRepository repository;
    private final ExceptionMessages messages;
    private final WebSecurityConfig webSecurityConfig;
    private final UserRepository userRepository;
    private final GoogleTokenRepository googleTokenRepository;

    @Override
    public UserDTO createUser(String email,String name, String password) {

        if (repository.findUserByEmail(email).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail(email);
        user.setUserStatus(UserStatus.GOOGLE_NOT_VERIFICATED);
        user.setPassword(webSecurityConfig.passwordEncoder().encode(password));
        user.setRole(Roles.ROLE_USER.toString());
        return User.userToDTO(repository.save(user));
    }
    @Override
    public UserDTO getUserByEmail(String email){
        User user = repository.findUserByEmail(email).orElseThrow(()-> new NotfoundException(messages.getUSER_NOT_FOUND_404()));
        var dto = User.userToDTO(user);

        if (user.getUserStatus()==UserStatus.GOOGLE_VERIFICATED){
            GoogleToken gt = googleTokenRepository.findById(user.getGoogleTokenId()).orElseThrow(()->new NotfoundException("You shouldn't see this"));
            dto.setGoogleTokenEmailAddress(gt.getOauth2EmailAddress());
        }

        return dto;
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = repository.findUserByEmail(id).orElseThrow(()-> new NotfoundException(messages.getUSER_NOT_FOUND_404()));
        var dto = User.userToDTO(user);
        return dto;
    }

    @Override
    public String deleteUser(String email){
        User user = repository.findUserByEmail(email).orElseThrow(()-> new NotfoundException(messages.getUSER_NOT_FOUND_404()));
        repository.delete(user);
        return "An user deleted with given email adress:" + user.getEmail();
    }


    public List<UserDTO> getAllUsers(){
        var allusers = repository.findAll();
        return usersToDTOS(allusers);
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        User user = repository.findUserByEmail(email).orElseThrow(()-> new NotfoundException(messages.getUSER_NOT_FOUND_404()));
        return Optional.of(userToDTO(user));
    }





}
