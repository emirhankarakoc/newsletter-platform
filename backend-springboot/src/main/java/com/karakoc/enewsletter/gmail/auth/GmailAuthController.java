package com.karakoc.enewsletter.gmail.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.karakoc.enewsletter.exceptions.general.UnauthorizatedException;
import com.karakoc.enewsletter.gmail.googletoken.GoogleTokenService;
import com.karakoc.enewsletter.security.TokenManager;
import com.karakoc.enewsletter.security.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/connect/google")
@RequiredArgsConstructor
public class GmailAuthController {

    private final GmailAuthService gmailAuthService;
    private final TokenManager tokenManager;
    private final GoogleTokenService googleTokenService;

    @Value("${emirhan-karakoc.url.backend}")
    private String backendUrl;
    @Value("${emirhan-karakoc.url.frontend}")
    private String frontendUrl;

    @PostMapping
    public Map<String, String> getRedirectUrl(@AuthenticationPrincipal UserPrincipal user) {
        if (user == null) {
            throw new UnauthorizatedException("JWT eksik. Giriş yapmamışsın.");
        }

        String token = tokenManager.issue(
                user.getUserId(),
                user.getEmail(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );

        String encoded = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
        String url = gmailAuthService.buildAuthorizationUrl(encoded);

        return Map.of("url", url);
    }

    @GetMapping("/callback")
    public void callback(
            @RequestParam("code") String code,
            @RequestParam("state") String encodedToken,
            HttpServletResponse response
    ) throws IOException {
        String token = new String(Base64.getDecoder().decode(encodedToken), StandardCharsets.UTF_8);
        DecodedJWT jwt = tokenManager.decode(token);
        String userId = jwt.getSubject();
        String loginEmail = jwt.getClaim("e").asString();

        // Gmail hesabı ile bağlantı kurulur ve ilgili token kaydedilir (email fark etmeksizin)
        gmailAuthService.exchangeCodeAndSaveToken(code, userId);

        // Kullanıcının giriş email'i ile devam ediyoruz, Gmail hesabı farklı olabilir
        String newToken = tokenManager.issue(userId, loginEmail, jwt.getClaim("a").asList(String.class));
        String redirectUrl = frontendUrl + "/oauth-success#token=" + newToken;
        response.sendRedirect(redirectUrl);
    }

    @PutMapping("/disconnect")
    public void disconnect(@AuthenticationPrincipal UserPrincipal user) {
        if (user == null) {
            throw new UnauthorizatedException("Yetkisiz erişim.");
        }

        googleTokenService.deleteGoogleAccount(user.getUserId());
        log.info("Gmail bağlantısı kaldırıldı: {}", user.getEmail());
    }
}
