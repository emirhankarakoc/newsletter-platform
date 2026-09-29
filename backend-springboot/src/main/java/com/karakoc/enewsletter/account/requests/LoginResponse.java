package com.karakoc.enewsletter.account.requests;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String accessToken;
    private String role;
}
