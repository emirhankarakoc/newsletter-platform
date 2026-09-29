package com.karakoc.enewsletter.gmail.mail;

import com.karakoc.enewsletter.cloudflare.R2Service;
import com.karakoc.enewsletter.customers.Customer;
import com.karakoc.enewsletter.exceptions.general.NotfoundException;
import com.karakoc.enewsletter.gmail.auth.GmailAuthService;
import com.karakoc.enewsletter.gmail.googletoken.GoogleToken;
import com.karakoc.enewsletter.gmail.googletoken.GoogleTokenService;
import com.karakoc.enewsletter.newsletters.Newsletter;
import com.karakoc.enewsletter.newsletters.NewsletterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailMailManager implements GmailMailService {

    private final GmailAuthService gmailAuthService;
    private final NewsletterRepository newsletterRepository;
    private final R2Service r2Service;

    private String replaceTokens(String template, Map<String, String> tokens) {
        String result = template;
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public void sendMail(GoogleToken googleToken, String to, String subject, String body, String newsletterId, Customer c) throws Exception {
        Newsletter newsletter = newsletterRepository.findById(newsletterId).orElseThrow(()->new NotfoundException("We cant find your newsletter, you shouldnt see this error."));
         ensureValidToken(googleToken);
        Map<String, String> tokens = Map.of(
                "ELECTRONICNEWSLETTERSERVICE_USERNAME", c.getName(),
                "ELECTRONICNEWSLETTERSERVICE_USERMAIL", to,
                "ELECTRONICNEWSLETTER_USERID", c.getId(),
                "ELECTRONICNEWSLETTER_NEWSLETTERID", newsletterId,
                "ELECTRONICNEWSLETTER_NEWSLETTERIMAGE", r2Service.getPublicUrl(newsletter.getImageKey()),
                "ELECTRONICNEWSLETTER_NEWSLETTERCUSTOMER_COUNT", String.valueOf(newsletter.getCustomers().size()),
                "ELECTRONICNEWSLETTER_NEWSLETTERDESCRIPTION", newsletter.getDescription()
        );

        String customizedBody = replaceTokens(body, tokens);

        String message = (
                "MIME-Version: 1.0\n" +
                        "Content-Type: text/html; charset=UTF-8\n" +
                        "From: %s\n".formatted(googleToken.getOauth2EmailAddress()) +
                        "To: %s\n".formatted(to) +
                        "Subject: %s\n".formatted(subject) +
                        "\n" +
                        customizedBody
        );

        String raw = Base64.getUrlEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://gmail.googleapis.com/gmail/v1/users/me/messages/send"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + googleToken.getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                    {
                      "raw": "%s"
                    }
                """.formatted(raw)))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 202) {
            throw new RuntimeException("Failed to send Gmail message: " + response.body());
        }
    }

    public void sendMailWithAttachment(GoogleToken gt, String to, String subject, String body, String cvUrl, String cvFileName) throws Exception {
        gt = ensureValidToken(gt);

        // Get the file from public URL
        URL url = new URL(cvUrl);
        InputStream inputStream = url.openStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();

        String encodedAttachment = Base64.getEncoder().encodeToString(buffer.toByteArray());

        // MIME message
        String boundary = "boundary-example-1";
        String rawMessage = """
                MIME-Version: 1.0
                Content-Type: multipart/mixed; boundary=%s
                From: %s
                To: %s
                Subject: %s

                --%s
                Content-Type: text/plain; charset="UTF-8"
                Content-Transfer-Encoding: 7bit

                %s

                --%s
                Content-Type: application/pdf
                Content-Disposition: attachment; filename="%s"
                Content-Transfer-Encoding: base64

                %s

                --%s--
                """.formatted(boundary, gt.getOauth2EmailAddress(), to, subject, boundary, body, boundary, cvFileName, encodedAttachment, boundary);

        String base64Raw = Base64.getUrlEncoder().encodeToString(rawMessage.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://gmail.googleapis.com/gmail/v1/users/me/messages/send"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + gt.getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                    {
                      "raw": "%s"
                    }
                """.formatted(base64Raw)))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 202) {
            throw new RuntimeException("Failed to send Gmail message with attachment: " + response.body());
        }
    }
    private GoogleToken ensureValidToken(GoogleToken token) {
        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(Instant.now())) {
            log.info("Token süresi geçmiş, refresh ediliyor: {}", token.getOauth2EmailAddress());
            String newAccessToken = gmailAuthService.refreshAccessToken(token);
            token.setAccessToken(newAccessToken);
            return token; // DB'ye zaten refreshAccessToken içinde kaydediliyor
        }
        return token;
    }

}
