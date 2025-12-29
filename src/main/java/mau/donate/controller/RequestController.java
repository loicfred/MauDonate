package mau.donate.controller;

import mau.donate.objects.Campaign;
import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import mau.donate.service.EmailService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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


    @PostMapping("/admin/donation/{id}/accept")
    public String acceptDonationRequest(Model model, Principal loggedUser, @PathVariable Long id) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation_Request req = Donation_Request.getById(Donation_Request.class, id).orElseThrow();
        req.Approved = true;
        req.UpdateOnly("Approved");

        User sender = req.getUser();
        emailService.acceptRequest(sender.getEmail(), sender.getFirstName() + " " + sender.getLastName());

        return "redirect:/admin?page=1";
    }
    @PostMapping("/admin/donation/{id}/deny")
    public String denyDonationRequest(Model model, Principal loggedUser, @PathVariable Long id) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation_Request req = Donation_Request.getById(Donation_Request.class, id).orElseThrow();
        req.Approved = false;
        req.UpdateOnly("Approved");

        User sender = req.getUser();
        emailService.denyRequest(sender.getEmail(), sender.getFirstName() + " " + sender.getLastName());

        return "redirect:/admin?page=1";
    }
}
