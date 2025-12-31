package mau.donate.controller.admin;

import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import mau.donate.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static mau.donate.controller.AppController.addEssential;

@CrossOrigin(origins = "*")
@Controller
public class RequestController {

    private final EmailService emailService;

    public RequestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/donation/request")
    public String makeRequest(Model model, Principal loggedUser, Donation_Request req) {
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);

        req.Approved = false;
        if (req.Write() > 0) return "redirect:/home?page=0&request=1";
        else return "redirect:/home?page=0&request=0";
    }


    @PostMapping("/admin/request/validate/{id}/accept")
    public String acceptDonationReqRequest(Model model, Principal loggedUser, @PathVariable Long id, @RequestParam String message, RedirectAttributes redirectAttributes) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation_Request req = Donation_Request.getById(Donation_Request.class, id).orElseThrow();
        req.Approved = true;
        req.UpdateOnly("Approved");

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
        req.Approved = false;
        req.UpdateOnly("Approved");

        User sender = req.getUser();
        emailService.denyRequest(sender.getEmail(), sender.getFirstName() + " " + sender.getLastName(), message);

        redirectAttributes.addFlashAttribute("successReq", "Successfully denied the request from " + sender.getFirstName() + ".");
        return "redirect:/admin?page=1";
    }
}
