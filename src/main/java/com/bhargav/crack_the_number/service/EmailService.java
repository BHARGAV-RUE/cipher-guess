package com.bhargav.crack_the_number.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("bhargavkallepally9@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Welcome to CipherGuess!");
            helper.setText(buildEmailTemplate(username), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("Email failed: " + e.getMessage());
        }
    }

    private String buildEmailTemplate(String username) {
        String template = """
            <!DOCTYPE html>
            <html>
            <head>
                <link href="https://fonts.googleapis.com/css2?family=Bebas+Neue&family=Rajdhani:wght@400;600;700&display=swap" rel="stylesheet">
            </head>
            <body style="margin:0; padding:0; background-color:#0a0a0a; font-family:'Rajdhani', sans-serif;">

                <div style="max-width:600px; margin:0 auto; background-color:#0a0a0a; border:1px solid rgba(255,215,0,0.2);">

                    <!-- Header -->
                    <div style="background-color:#111111; padding:40px; text-align:center; border-bottom:2px solid #FFD700;">
                        <h1 style="font-family:'Bebas Neue', cursive; font-size:48px; color:#FFD700; letter-spacing:8px; margin:0;">
                            CiPhErGuEsS
                        </h1>
                        <p style="font-family:monospace; font-size:11px; letter-spacing:4px; color:#888888; margin:8px 0 0;">
                            NUMBER GUESSING GAME
                        </p>
                    </div>

                    <!-- Body -->
                    <div style="padding:50px 40px;">

                        <p style="font-family:monospace; font-size:11px; letter-spacing:4px; color:#888888; margin:0 0 12px;">
                            WELCOME ABOARD
                        </p>
                        <h2 style="font-family:'Bebas Neue', cursive; font-size:52px; color:#FFD700; letter-spacing:4px; margin:0 0 24px; line-height:1;">
                            %s
                        </h2>

                        <p style="font-size:16px; color:#cccccc; line-height:1.8; margin:0 0 24px;">
                            You've successfully joined <strong style="color:#FFD700;">CipherGuess</strong> —
                            the number guessing game where your instincts are put to the test.
                        </p>

                        <!-- Divider -->
                        <div style="width:40px; height:3px; background-color:#FFD700; margin:32px 0;"></div>

                        <!-- Difficulties -->
                        <p style="font-family:monospace; font-size:11px; letter-spacing:4px; color:#888888; margin:0 0 16px;">
                            AVAILABLE DIFFICULTIES
                        </p>

                        <table style="width:100%%; border-collapse:collapse;">
                            <tr>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#FFD700; font-family:'Bebas Neue',cursive; font-size:20px; letter-spacing:2px;">EASY</td>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#888888; font-family:monospace; font-size:12px;">1 — 10</td>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#cccccc; font-size:14px;">Warm up</td>
                            </tr>
                            <tr>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#FFD700; font-family:'Bebas Neue',cursive; font-size:20px; letter-spacing:2px;">MEDIUM</td>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#888888; font-family:monospace; font-size:12px;">1 — 50</td>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#cccccc; font-size:14px;">Getting hot</td>
                            </tr>
                            <tr>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#FFD700; font-family:'Bebas Neue',cursive; font-size:20px; letter-spacing:2px;">HARD</td>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#888888; font-family:monospace; font-size:12px;">1 — 100</td>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#cccccc; font-size:14px;">Real challenge</td>
                            </tr>
                            <tr>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#ff4444; font-family:'Bebas Neue',cursive; font-size:20px; letter-spacing:2px;">GOD</td>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#888888; font-family:monospace; font-size:12px;">1 — 1000</td>
                                <td style="padding:12px 16px; border:1px solid rgba(255,215,0,0.2); color:#cccccc; font-size:14px;">Are you serious?</td>
                            </tr>
                        </table>

                        <!-- Divider -->
                        <div style="width:40px; height:3px; background-color:#FFD700; margin:32px 0;"></div>

                        <p style="font-size:15px; color:#888888; line-height:1.8; margin:0 0 32px;">
                            Head to your dashboard, pick a difficulty and start cracking.
                            Every game is tracked — can you top your own best score?
                        </p>

                        <!-- CTA Button -->
                        <div style="text-align:center; margin:32px 0;">
                            <a href="http://localhost:5173"
                               style="display:inline-block; background-color:#FFD700; color:#000000; font-family:'Bebas Neue',cursive; font-size:20px; letter-spacing:4px; padding:16px 48px; text-decoration:none;">
                                PLAY NOW
                            </a>
                        </div>
                    </div>

                    <!-- Footer -->
                    <div style="background-color:#111111; padding:28px 40px; border-top:1px solid rgba(255,215,0,0.2); text-align:center;">
                        <p style="font-family:monospace; font-size:11px; letter-spacing:3px; color:#888888; margin:0 0 8px;">
                            DEVELOPED BY BHARGAV
                        </p>
                        <p style="font-size:12px; color:#555555; margin:0;">
                            CipherGuess — Crack The Number
                        </p>
                    </div>

                </div>
            </body>
            </html>
            """;

        return String.format(template, username.toUpperCase());
    }
}