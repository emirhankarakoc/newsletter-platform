package com.karakoc.enewsletter.gmail.mail;

import com.karakoc.enewsletter.customers.Customer;
import com.karakoc.enewsletter.gmail.googletoken.GoogleToken;

public interface GmailMailService {
    void sendMail(GoogleToken googleToken, String to, String subject, String body,String newsletterId, Customer c) throws Exception;
    void sendMailWithAttachment(GoogleToken gt, String to, String subject, String body, String cvUrl, String cvFileName) throws Exception;

}
