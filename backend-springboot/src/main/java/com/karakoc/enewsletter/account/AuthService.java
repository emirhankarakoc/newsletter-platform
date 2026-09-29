package com.karakoc.enewsletter.account;

import com.karakoc.enewsletter.account.requests.LoginResponse;
import com.karakoc.enewsletter.user.UserDTO;

public interface AuthService {
    LoginResponse attemptLogin(String email, String password);
    UserDTO attemptRegister(String email,String name, String password);
}
