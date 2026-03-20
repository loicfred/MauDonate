package mau.donate.controller;

import jakarta.servlet.http.HttpServletRequest;
import mau.donate.objects.Email_Verification;
import mau.donate.objects.Notification;
import mau.donate.objects.User;
import mau.donate.service.EmailService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.UUID;

@Controller
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthController(PasswordEncoder passwordEncoder, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/accounts/login")
    public String login(Principal loggedUser) {
        if (loggedUser != null) return "redirect:/";
        return "accounts/login";
    }



    @GetMapping("/accounts/signup")
    public String signupForm(Model model, Principal loggedUser) {
        if (loggedUser != null) return "redirect:/";
        model.addAttribute("user", new User());
        return "accounts/signup";
    }
    @PostMapping("/accounts/signup")
    public String register(HttpServletRequest request, User user, Principal loggedUser) {
        if (!user.isPasswordValid()) return "redirect:/accounts/signup?badpassword";
        if (User.getByEmail(user.Email) != null) return "redirect:/accounts/signup?bademail";
        if (user.getAge() < 18) return "redirect:/accounts/signup?badage";
        User.ClearFailedLogins(user.Email);
        user.Password = passwordEncoder.encode(user.Password);
        user.Write();
        String token = UUID.randomUUID().toString();
        new Email_Verification(user, token, "REGISTRATION");
        emailService.sendVerificationEmail(request, user.Email, token);
        return "redirect:/accounts/login?verify";
    }
    @Transactional
    @GetMapping("/accounts/email/verify")
    public String verifyAccount(@RequestParam String token, Model model) {
        Email_Verification vToken = Email_Verification.getByToken(token);
        if (vToken == null) {
            model.addAttribute("error", "Verification code has expired.");
            return "accounts/verification";
        } else {
            vToken.getUser().setEnabled(true);
            vToken.getUser().setVerified(true);
            vToken.getUser().UpdateOnly("Verified", "Enabled");
            vToken.Delete();
            new Notification(vToken.getUser().getID(), "Your account has been verified!", "Your account has been verified successfully! You can now log in.");
            model.addAttribute("success", "Your account has been verified! You can now log in.");
            model.addAttribute("loginparam", "verified");
            return "accounts/verification";
        }
    }

    @GetMapping("/accounts/delete/verify")
    public String deleteAccount(@RequestParam(required = false) String token, Model model) {
        Email_Verification vToken = Email_Verification.getByToken(token);
        if (vToken == null) {
            model.addAttribute("error", "Verification code has expired.");
            return "accounts/verification";
        } else {
            vToken.getUser().Delete();
            vToken.Delete();
            SecurityContextHolder.clearContext();
            model.addAttribute("success", "Your account has been deleted successfully! You can now sign in once again.");
            model.addAttribute("loginparam", "deleted");
            return "accounts/verification";
        }
    }



    @GetMapping("/accounts/resetpassword")
    public String resetPassword(Model model) {
        model.addAttribute("user", new User());
        return "accounts/resetpassword";
    }
    @PostMapping("/accounts/resetpassword")
    public String resetPassword(HttpServletRequest request, RedirectAttributes model, @ModelAttribute User user, @RequestParam String type) {
        if (type.equals("email")) {
            User u = User.getByEmail(user.Email);
            if (u == null) {
                model.addFlashAttribute("error", "No account with this email exists.");
                return "redirect:/accounts/resetpassword";
            }
            String token = UUID.randomUUID().toString();
            new Email_Verification(u, token, "PASSWORDRESET");
            emailService.sendResetPasswordEmail(request, u.Email, token);

            model.addFlashAttribute("success", "An email has been sent to your email address with further instructions.");
        } else if (type.equals("phone")) {
            User u = User.getByPhone(user.Phone);
            if (u == null) {
                model.addFlashAttribute("error", "No account with this phone number exists.");
                return "redirect:/accounts/resetpassword";
            }

            model.addFlashAttribute("success", "An SMS has been sent to your phone number with further instructions.");
        }
        return "redirect:/accounts/resetpassword";
    }


    @GetMapping("/accounts/newpassword")
    public String newPassword(RedirectAttributes model, @RequestParam String token) {
        Email_Verification vToken = Email_Verification.getByToken(token);
        if (vToken == null) {
            model.addFlashAttribute("error", "The reset code has expired.");
            return "redirect:/accounts/resetpassword";
        } else {
            return "accounts/newpassword";
        }
    }
    @PostMapping("/accounts/newpassword")
    public String newPassword(RedirectAttributes model, @RequestParam String token, @RequestParam String password1, @RequestParam String password2) {
        Email_Verification vToken = Email_Verification.getByToken(token);
        if (vToken == null) {
            model.addAttribute("error", "The reset code has expired.");
            return "redirect:/accounts/resetpassword";
        } else {
            if (!password1.equals(password2)) {
                model.addAttribute("error", "The new password doesn't match.");
                return "redirect:/accounts/newpassword?token=" + token;
            }
            vToken.getUser().setPassword(passwordEncoder.encode(password1));
            if (!vToken.getUser().isPasswordValid()) {
                model.addAttribute("error", "Password should be of minimum 8 of length and contain at least 1 digit, symbol, uppercase & lowercase character.");
                return "redirect:/accounts/newpassword?token=" + token;
            }
            vToken.getUser().UpdateOnly("Password");
            new Notification(vToken.getUser().getID(), "Your password has been reset!", "Your password has been reset successfully.");
            vToken.Delete();
            return "redirect:/accounts/login?newpassword";
        }
     }
}
