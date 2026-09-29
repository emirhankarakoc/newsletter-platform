# Newsletter Platform

This app lets a creator make a newsletter, collect subscribers, and send an HTML email from a connected Gmail account.

## What it does

1. The creator adds a newsletter with a name, description, and image.
2. Readers subscribe. The backend checks for duplicate subscriptions and supports unsubscribe.
3. The creator connects a Google account with OAuth.
4. The creator uploads an HTML email. The backend adds subscriber details to the text and sends it through the Gmail API.

The app uses its own JWT login for users. Google OAuth is only for connecting a Gmail account and sending mail.

## Tech and code

Java 17, Spring Boot, MySQL, JPA, Spring Security/JWT, Google OAuth, Gmail API, React, TypeScript, and Vite. Images use the AWS S3 SDK with **Cloudflare R2**, an S3-compatible service. 

- Newsletters and subscribers: `backend-springboot/src/main/java/com/karakoc/enewsletter/newsletters/` and `customers/`
- Google connection: `backend-springboot/src/main/java/com/karakoc/enewsletter/gmail/auth/`
- Gmail sending: `backend-springboot/src/main/java/com/karakoc/enewsletter/gmail/mail/GmailMailManager.java`
- React pages: `frontend-reactjs/src/pages/`

## Run locally

You need Java 17, Maven, MySQL, Node.js, a Google OAuth client, and R2 settings if you want to upload images. The OAuth client's callback URL must point to `/connect/google/callback` on your backend.

Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, and `GOOGLE_REDIRECT_URI` for the backend. Add `R2_*` values for images. All backend settings are in `backend-springboot/src/main/resources/application.properties`.

```bash
cd backend-springboot
mvn spring-boot:run
```

In a second terminal, open `frontend-reactjs/`. Set `VITE_GOOGLE_CLIENT_ID` to the same Google client ID, then run `npm install` and `npm run dev`. The local API and client URL defaults are `http://localhost:8080` and `http://localhost:5173`; use `VITE_API_URL` and `VITE_CLIENT_URL` to change them.

