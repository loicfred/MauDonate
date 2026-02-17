package mau.donate.controller.admin.donate;

import mau.donate.objects.Donation_Request;
import mau.donate.objects.Notification;
import mau.donate.objects.User;
import mau.donate.objects.derived.D_Donation_Request;
import mau.donate.objects.enums.DonationStatus;
import mau.donate.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

import static mau.donate.config.AppConfig.dbService;
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
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);

        if (U.getPhone() == null) return "redirect:/settings?phoneNumber";
        else if (U.getAddress() == null) return "redirect:/settings?address";
        model.addAttribute("request", new Donation_Request());
        return "request";
    }

    @PostMapping("/donation/request")
    public String makeRequest(Model model, Principal loggedUser, @ModelAttribute Donation_Request req, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);

        req.CreatedAt = LocalDateTime.now();
        req.UpdatedAt = LocalDateTime.now();
        req.Status = DonationStatus.PENDING.toString();
        req.UserID = U.getID();
        req.Approved = false;
        req.Completed = false;
        if (req.Write() > 0) {
            redirectAttributes.addFlashAttribute("successDon", "Successfully sent a donation request. Once it's approved you will receive an email.");
            return "redirect:/home?page=0";
        } else {
            redirectAttributes.addFlashAttribute("failed", "Failed to send a donation request. Try again later.");
            return "redirect:/donation/request";
        }
    }


    @PostMapping("/admin/request/validate/{id}/accept")
    public String acceptDonationRequest(Model model, Principal loggedUser, @PathVariable Long id, @RequestParam String message, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation_Request req = Donation_Request.getById(Donation_Request.class, id).orElseThrow();
        req.UpdatedAt = LocalDateTime.now();
        req.Approved = true;
        req.UpdateOnly("Approved", "UpdatedAt");
        dbService.refreshListOfClass(D_Donation_Request.class);

        User requester = req.getUser();
        emailService.acceptRequest(requester.getEmail(), requester.getFirstName() + " " + requester.getLastName(), message);
        new Notification(requester.getID(), "Request Approved", "Congratulations, your donation request has been approved !");

        redirectAttributes.addFlashAttribute("successReq", "Successfully accepted the request from " + requester.getFirstName() + ".");
        return "redirect:/admin?page=1";
    }
    @PostMapping("/admin/request/validate/{id}/deny")
    public String denyDonationRequest(Model model, Principal loggedUser, @PathVariable Long id, @RequestParam String message, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation_Request req = Donation_Request.getById(Donation_Request.class, id).orElseThrow();
        req.Delete();

        User requester = req.getUser();
        emailService.denyRequest(requester.getEmail(), requester.getFirstName() + " " + requester.getLastName(), message);
        new Notification(requester.getID(), "Denied Request", "Unfortunately your donation request has been denied. More details sent by email.");

        redirectAttributes.addFlashAttribute("successReq", "Successfully denied the request from " + requester.getFirstName() + ".");
        return "redirect:/admin?page=1";
    }
}
