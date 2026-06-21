package mau.donate.controller;

import mau.donate.service.EmailService;
import org.solarframework.web.auth.obj.Account_User;
import org.solarframework.web.auth.spring.v1.AuthController;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Objects;

import static org.solarframework.web.auth.spring.v1.AuthController.validatePassword;
import static org.solarframework.core.util.NumberUtils.Range;
import static org.solarframework.web.auth.spring.Constants.addEssential;

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
        if (loggedUser == null) return "redirect:/auth/v1/login";
        Account_User U = Account_User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        model.addAttribute("user", U);
        return "settings";
    }

    @PostMapping("/settings")
    @CacheEvict(value = "IMG", key = "'PFP' + #newDetails.ID")
    public String updateSettings(Model model, Principal loggedUser, @ModelAttribute AuthController.UserModel newDetails, @RequestParam(value = "pfp", required = false) MultipartFile image, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/auth/v1/login";
        Account_User oldYou = Account_User.getByAuthentication(loggedUser);
        try {
            if (!oldYou.getEmail().equals(newDetails.Email) && Account_User.getByEmail(newDetails.Email) != null) {
                redirectAttributes.addFlashAttribute("error", "Email already exists.");
                return "redirect:/settings";
            }

            if (!newDetails.Password.isBlank()) {
                if (!validatePassword(newDetails.Password)) {
                    redirectAttributes.addFlashAttribute("error", "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character.");
                    return "redirect:/settings";
                }
                oldYou.setPasswordHash(passwordEncoder.encode(newDetails.Password));
                oldYou.UpdateOnly("Password");
            }

            if (newDetails.DateOfBirth != null) oldYou.setDateOfBirth(newDetails.DateOfBirth);

            if (!Range(newDetails.FirstName.length(), 1, 64)
                    || !Range(newDetails.LastName.length(), 1, 64)
                    || !Range(newDetails.Address.length(), 1, 128)
                    || !Range(newDetails.Phone.length(), 1, 20)) {
                redirectAttributes.addFlashAttribute("error", "Invalid inputs.");
                return "redirect:/settings";
            }
            oldYou.setFirstName(newDetails.FirstName);
            oldYou.setLastName(newDetails.LastName);
            oldYou.setGender(newDetails.Gender);
            oldYou.setPhoneNumber(newDetails.Phone);

            oldYou.UpdateOnly("Email", "FirstName", "LastName", "Gender", "Phone", "DateOfBirth");

            if (image != null && Objects.equals(image.getContentType(), "image/png")) {
                oldYou.setAvatar(image.getBytes());
                oldYou.UpdateOnly("Avatar");
            }
            Account_User newYou = Account_User.getByEmail(newDetails.Email);

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
//        if (loggedUser == null) return "redirect:/auth/v1/login";
//        Account_User U = Account_User.getByAuthentication(loggedUser);
//
//        String token = UUID.randomUUID().toString();
//        new Email_Verification(U, token, "DELETE ACCOUNT");
//        emailService.sendDeleteConfirmationEmail(request, U.Email, token);
//
//        redirectAttributes.addFlashAttribute("success", "Account deletion verification email sent.");
//        return "redirect:/settings";
//    }
}
