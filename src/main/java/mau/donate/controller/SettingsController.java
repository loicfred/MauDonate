package mau.donate.controller;

import jakarta.servlet.http.HttpServletRequest;
import mau.donate.objects.Email_Verification;
import mau.donate.objects.User;
import mau.donate.service.EmailService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static mau.donate.controller.AppController.addEssential;
import static my.loic.utilities.util.Utilities.Range;

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
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        model.addAttribute("user", U);
        return "settings";
    }

    @PostMapping("/settings")
    @CacheEvict(value = "IMG", key = "'PFP' + #newDetails.ID")
    public String updateSettings(Model model, Principal loggedUser, @ModelAttribute User newDetails, @RequestParam(value = "pfp", required = false) MultipartFile image, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User oldYou = User.getByAuthentication(loggedUser);
        try {
            if (!oldYou.Email.equals(newDetails.Email) && User.getByEmail(newDetails.Email) != null) {
                redirectAttributes.addFlashAttribute("error", "Email already exists.");
                return "redirect:/settings";
            }

            if (!newDetails.Password.isBlank()) {
                if (!newDetails.isPasswordValid()) {
                    redirectAttributes.addFlashAttribute("error", "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character.");
                    return "redirect:/settings";
                }
                newDetails.Password = passwordEncoder.encode(newDetails.Password);
                newDetails.UpdateOnly("Password");
            }

            if (newDetails.DateOfBirth == null) newDetails.DateOfBirth = oldYou.DateOfBirth;

            if (!Range(newDetails.FirstName.length(), 1, 64)
                    || !Range(newDetails.LastName.length(), 1, 64)
                    || !Range(newDetails.Address.length(), 1, 128)
                    || !Range(newDetails.Phone.length(), 1, 20)) {
                redirectAttributes.addFlashAttribute("error", "Invalid inputs.");
                return "redirect:/settings";
            }

            newDetails.ID = oldYou.ID;
            newDetails.UpdatedAt = LocalDateTime.now();
            newDetails.UpdateOnly("Email", "FirstName", "LastName", "Address", "Gender", "Phone", "DateOfBirth", "Anonymous", "Enabled");

            if (image != null && Objects.equals(image.getContentType(), "image/png")) {
                newDetails.Image = image.getBytes();
                newDetails.UpdateOnly("Image");
            }
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

//    @PostMapping("/delete-account")
//    public String deleteAccount(HttpServletRequest request, Principal loggedUser, RedirectAttributes redirectAttributes) {
//        if (loggedUser == null) return "redirect:/accounts/login";
//        User U = User.getByAuthentication(loggedUser);
//
//        String token = UUID.randomUUID().toString();
//        new Email_Verification(U, token, "DELETE ACCOUNT");
//        emailService.sendDeleteConfirmationEmail(request, U.Email, token);
//
//        redirectAttributes.addFlashAttribute("success", "Account deletion verification email sent.");
//        return "redirect:/settings";
//    }
}
