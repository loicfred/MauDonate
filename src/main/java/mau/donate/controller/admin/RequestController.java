package mau.donate.controller.admin;

import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import mau.donate.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;

import static mau.donate.controller.AppController.addEssential;

@CrossOrigin(origins = "*")
@Controller
public class RequestController {

    private final EmailService emailService;

    public RequestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/donation/request")
    public String requestPage(Model model, Principal loggedUser) {
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);

        if (U.getPhone() == null) return "redirect:/settings?phoneNumber";
        else if (U.getAddress() == null) return "redirect:/settings?address";
        model.addAttribute("request", new Donation_Request());
        return "request";
    }

    @PostMapping("/donation/request")
    public String makeRequest(Model model, Principal loggedUser, @ModelAttribute Donation_Request req, RedirectAttributes redirectAttributes) {
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);

        req.CreatedAt = LocalDateTime.now();
        req.UpdatedAt = LocalDateTime.now();
        req.UserID = U.getID();
        req.Approved = false;
        if (req.Write() > 0) {
            redirectAttributes.addFlashAttribute("successReq", "Successfully sent a donation request. Once it's approved you will receive an email.");
            return "redirect:/home?page=0&requestSuccess";
        } else {
            redirectAttributes.addFlashAttribute("failedReq", "Failed to send a donation request. Try again later.");
            return "redirect:/donation/request?requestSuccess";
        }
    }


    @PostMapping("/admin/request/validate/{id}/accept")
    public String acceptDonationReqRequest(Model model, Principal loggedUser, @PathVariable Long id, @RequestParam String message, RedirectAttributes redirectAttributes) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation_Request req = Donation_Request.getById(Donation_Request.class, id).orElseThrow();
        req.UpdatedAt = LocalDateTime.now();
        req.Approved = true;
        req.UpdateOnly("Approved", "UpdatedAt");

        User sender = req.getUser();
        emailService.acceptRequest(sender.getEmail(), sender.getFirstName() + " " + sender.getLastName(), message);

        redirectAttributes.addFlashAttribute("successReq", "Successfully accepted the request from " + sender.getFirstName() + ".");
        return "redirect:/admin?page=1";
    }
    @PostMapping("/admin/request/validate/{id}/deny")
    public String denyDonationReqRequest(Model model, Principal loggedUser, @PathVariable Long id, @RequestParam String message, RedirectAttributes redirectAttributes) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation_Request req = Donation_Request.getById(Donation_Request.class, id).orElseThrow();
        req.UpdatedAt = LocalDateTime.now();
        req.Approved = false;
        req.UpdateOnly("Approved", "UpdatedAt");

        User sender = req.getUser();
        emailService.denyRequest(sender.getEmail(), sender.getFirstName() + " " + sender.getLastName(), message);

        redirectAttributes.addFlashAttribute("successReq", "Successfully denied the request from " + sender.getFirstName() + ".");
        return "redirect:/admin?page=1";
    }
}
