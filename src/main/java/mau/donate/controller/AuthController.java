package mau.donate.controller;


import mau.donate.objects.Email_Verification;
import mau.donate.objects.User;
import mau.donate.service.EmailService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthController(PasswordEncoder passwordEncoder, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/accounts/signup")
    public String signupForm(Model model) {
        model.addAttribute("isAnonymous", SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
        model.addAttribute("user", new User());
        return "accounts/signup";
    }
    @GetMapping("/accounts/verify")
    public String verifyAccount(@RequestParam("token") String token, Model model) {
        model.addAttribute("isAnonymous", SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
        Email_Verification vToken = Email_Verification.getByToken(token);
        if (vToken == null) {
            model.addAttribute("message", "Verification code has expired.");
            model.addAttribute("success", false);
        } else {
            vToken.getUser().setEnabled(true);
            vToken.getUser().Update();
            vToken.Delete();
            model.addAttribute("message", "Your account has been verified! You can now log in.");
            model.addAttribute("success", true);
        }
        return "accounts/verification";
    }
    @GetMapping("/accounts/login")
    public String login(Model model) {
        model.addAttribute("isAnonymous", SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
        return "accounts/login";
    }


    @PostMapping("/post/accounts/register")
    public String register(User user) {
        User.ClearFailedLogins(user.Username, user.Email);
        user.Password = passwordEncoder.encode(user.Password);
        user.Write();
        String token = UUID.randomUUID().toString();
        new Email_Verification(user, token, "REGISTRATION");
        emailService.sendVerificationEmail(user.Email, token);
        return "redirect:/accounts/login?verify";
    }
}
