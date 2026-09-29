package com.karakoc.enewsletter.user;


import com.karakoc.enewsletter.account.requests.OAuth2Response;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(String email,String name, String password);
    Optional<UserDTO> findUserByEmail(String email);
    UserDTO getUserByEmail(String email);
    UserDTO getUserById(String id);
    String deleteUser(String email);
    List<UserDTO> getAllUsers();
}
