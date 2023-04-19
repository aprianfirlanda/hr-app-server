package com.aprianfirlanda.hrappserver.services;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String message);
}
