package mau.donate.controller;

import mau.donate.objects.Email_Verification;
import mau.donate.objects.User;
import mau.donate.service.EmailService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

import static mau.donate.controller.AppController.addEssential;
import static my.utilities.util.Utilities.Range;

@CrossOrigin(origins = "*")
@Controller
public class SettingsController {

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public SettingsController(PasswordEncoder passwordEncoder, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/settings")
    public String settings(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByEmail(loggedUser.getName());
        addEssential(model, loggedUser, U);
        model.addAttribute("user", U);
        return "settings";
    }
    @PostMapping("/settings")
    public String updateSettings(@ModelAttribute User newDetails, Model model, Principal loggedUser, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        try {
            User oldYou = User.getByEmail(loggedUser.getName());
            if (!oldYou.Email.equals(newDetails.Email) && User.getByEmail(newDetails.Email) != null) {
                redirectAttributes.addFlashAttribute("error", "Email already exists.");
                return "redirect:/settings";
            }

            if (newDetails.Password.isBlank()) newDetails.Password = oldYou.Password;
            if (newDetails.DateOfBirth == null) newDetails.DateOfBirth = oldYou.DateOfBirth;


            if (!Range(newDetails.FirstName.length(), 1, 64)
                    || !Range(newDetails.LastName.length(), 1, 64)
                    || !Range(newDetails.Address.length(), 1, 128)
                    || !Range(newDetails.Phone.length(), 1, 20)) {
                redirectAttributes.addFlashAttribute("error", "Invalid inputs.");
                return "redirect:/settings";
            }
            if (!newDetails.isPasswordValid()) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character.");
                return "redirect:/settings";
            }

            newDetails.Password = passwordEncoder.encode(newDetails.Password);
            newDetails.ID = oldYou.ID;
            newDetails.UpdatedAt = LocalDateTime.now();
            newDetails.UpdateOnly("Email", "Password", "FirstName", "LastName", "Address", "Gender", "Phone", "DateOfBirth", "isAnonymous", "Enabled");
            User newYou = User.getByEmail(newDetails.Email);

            addEssential(model, loggedUser, newYou);
            model.addAttribute("user", newYou);
            redirectAttributes.addFlashAttribute("success", "Settings updated successfully.");
            return "redirect:/settings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid inputs.");
            return "redirect:/settings";
        }
    }

    @PostMapping("/delete-account")
    public String deleteAccount(Principal loggedUser, RedirectAttributes redirectAttributes) {
        if (loggedUser != null) {
            User user = User.getByEmail(loggedUser.getName());
            if (user != null) {

                String token = UUID.randomUUID().toString();
                new Email_Verification(user, token, "DELETE ACCOUNT");
                emailService.sendDeleteConfirmationEmail(user.Email, token);

                redirectAttributes.addFlashAttribute("success", "Account deletion verification email sent.");
                return "redirect:/settings";
            }
        }
        return "redirect:/settings";
    }
    @GetMapping("/accounts/delete/verify")
    public String verifyAccount(@RequestParam("token") String token, Model model) {
        model.addAttribute("isAnonymous", SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
        Email_Verification vToken = Email_Verification.getByToken(token);
        if (vToken == null) {
            model.addAttribute("message", "Verification code has expired.");
            model.addAttribute("success", false);
            return "/accounts/verification";
        } else {
            vToken.getUser().Delete();
            vToken.Delete();
            SecurityContextHolder.clearContext();
            model.addAttribute("message", "Your account has deleted successfully!");
            model.addAttribute("success", true);
            model.addAttribute("loginparam", "deleted");
            return "/accounts/verification";
        }
    }
}
