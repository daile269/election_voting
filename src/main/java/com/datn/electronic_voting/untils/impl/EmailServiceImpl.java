package com.datn.electronic_voting.untils.impl;

import com.datn.electronic_voting.untils.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Xác thực tài khoản");
        message.setText("Chào mừng bạn đến với hệ thống bỏ phiếu\n Mã xác thực của bạn là: " + code);
        message.setFrom("daile2692003@gmail.com");
        mailSender.send(message);
    }
}
