# Newsletter Platform

A Spring Boot and React application for creating newsletters, collecting subscribers, and sending personalized HTML messages through a connected Gmail account.

## What the code does

1. A creator registers and manages newsletters, descriptions, and images.
2. Readers subscribe; the backend checks duplicate subscriptions and supports unsubscribe.
3. The creator connects a Google account through OAuth. The backend stores the Google token and refreshes it when needed.
4. A send request takes an HTML template, replaces subscriber/newsletter placeholders, and calls the Gmail API for each recipient.

The app's own authentication uses Spring Security/JWT. Google OAuth is used separately to authorize Gmail sending.

## Stack and code map

- Java 17, Spring Boot 3.3, Spring Data JPA, MySQL, Spring Security/JWT
- Google OAuth and Gmail API
- React, TypeScript, Vite
- AWS SDK `S3Client` pointed at Cloudflare R2 for images (S3-compatible storage, not AWS hosting)

| Component | Path |
| --- | --- |
| Newsletter and subscription APIs | `backend-springboot/src/main/java/com/karakoc/enewsletter/newsletters/`, `customers/` |
| Google connection and token handling | `backend-springboot/src/main/java/com/karakoc/enewsletter/gmail/auth/`, `googletoken/` |
| Personalized Gmail delivery | `backend-springboot/src/main/java/com/karakoc/enewsletter/gmail/mail/GmailMailManager.java` |
| Client screens | `frontend-reactjs/src/pages/` |

## Local setup

Requires Java 17, Maven, MySQL, Node.js, a Google OAuth client with a callback URL configured for your backend, and your own R2-compatible storage values for image features.

Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GOOGLE_REDIRECT_URI`, `R2_ACCESS_KEY`, `R2_SECRET_KEY`, `R2_ENDPOINT`, `R2_BUCKET_NAME`, and `R2_PUBLIC_URL` for the backend. The complete property list is in `backend-springboot/src/main/resources/application.properties`. Use the same Google client ID in the frontend as `VITE_GOOGLE_CLIENT_ID`.

```bash
cd backend-springboot
mvn spring-boot:run

# In another terminal, from the repository root:
cd frontend-reactjs
npm install
VITE_API_URL=http://localhost:8080 VITE_CLIENT_URL=http://localhost:5173 \
VITE_GOOGLE_CLIENT_ID=your-client-id npm run dev
```

This is a portfolio prototype, not a hosted newsletter service or a claim of paying users. External Google/R2 setup is required for the complete send-and-image flow. The existing backend test is a Spring context smoke test; Gmail delivery is not covered by automated end-to-end tests here.
