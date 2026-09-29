package com.karakoc.enewsletter.account;

import com.karakoc.enewsletter.account.requests.LoginRequest;
import com.karakoc.enewsletter.account.requests.LoginResponse;
import com.karakoc.enewsletter.account.requests.OAuth2Response;
import com.karakoc.enewsletter.account.requests.RegisterRequest;
import com.karakoc.enewsletter.exceptions.general.UnauthorizatedException;
import com.karakoc.enewsletter.security.UserPrincipal;
import com.karakoc.enewsletter.user.UserDTO;
import com.karakoc.enewsletter.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    // Normal giriş
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.attemptLogin(request.getEmail(), request.getPassword());
    }

    // Normal kayıt
    @PostMapping("/register")
    public UserDTO register(@RequestBody RegisterRequest request) {
        return authService.attemptRegister(request.getEmail(),request.getName(), request.getPassword());
    }

    // Kullanıcının kendi bilgilerini getirme
    @GetMapping("/getme")
    public UserDTO getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getUserByEmail(principal.getEmail());
    }






}
