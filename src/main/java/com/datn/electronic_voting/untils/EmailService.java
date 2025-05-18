package com.datn.electronic_voting.untils;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String code);
    void sendNewPasswordToEmail(String toEmail,String newPassword,String username);
}
