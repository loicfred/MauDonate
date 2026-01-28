package mau.donate.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(HttpServletRequest req, String to, String token) {
        String subject = "Verify your MauDonate account";
        String verificationUrl = req.getRequestURL().toString().replaceFirst(req.getRequestURI(), "") + "/accounts/email/verify?token=" + token;

        String message = "Welcome! Please click the link below to verify your MauDonate account:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        System.out.println("Email sent to " + to + " with subject: " + subject + "\n" + message);
    }
    public void sendResetPasswordEmail(HttpServletRequest req, String to, String token) {
        String subject = "Reset your MauDonate account password";
        String verificationUrl = req.getRequestURL().toString().replaceFirst(req.getRequestURI(), "") + "/accounts/newpassword?token=" + token;

        String message = "Hello dear. To reset your password, please click the link below:\n" + verificationUrl + "\n";
        message += "\nIf you did not request a password reset, please ignore this email.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        System.out.println("Email sent to " + to + " with subject: " + subject + "\n" + message);
    }

    public void sendDeleteConfirmationEmail(HttpServletRequest req, String to, String token) {
        String subject = "Confirm deletion of MauDonate account";
        String verificationUrl = req.getRequestURL().toString().replaceFirst(req.getRequestURI(), "") + "/accounts/delete/verify?token=" + token;

        String message = "Welcome! Please click the link below to confirm the deletion of your MauDonate account:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        System.out.println("Email sent to " + to + " with subject: " + subject + "\n" + message);
    }

    public void acceptRequest(String to, String name, String msg) {
        String subject = "Approval of Donation Request";

        String message = "Greetings dear " + name + ". It is with great pleasure that we inform you that your donation request has been approved.\n" + msg + "\nMay you find the help you need.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        System.out.println("Email sent to " + to + " with subject: " + subject + "\n" + message);
    }
    public void denyRequest(String to, String name, String msg) {
        String subject = "Rejection of Donation Request";

        String message = "Greetings dear " + name + ". It is with great sadness that we inform you that your donation request has been denied.\n" + msg + "\nSee you next time.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        System.out.println("Email sent to " + to + " with subject: " + subject + "\n" + message);
    }

    public void acceptDonation(String to, String name, String msg) {
        String subject = "Approval of Donation";

        String message = "Greetings dear " + name + ". It is with great pleasure that we inform you that your donation has been approved.\n" + msg + "\nMay you find the help you need.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        System.out.println("Email sent to " + to + " with subject: " + subject + "\n" + message);
    }
    public void denyDonation(String to, String name, String msg) {
        String subject = "Rejection of Donation";

        String message = "Greetings dear " + name + ". It is with great sadness that we inform you that your donation has been denied.\n" + msg + "\nSee you next time.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        System.out.println("Email sent to " + to + " with subject: " + subject + "\n" + message);
    }
}