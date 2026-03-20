package mau.donate.controller.admin.donate;

import mau.donate.objects.*;
import mau.donate.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static mau.donate.controller.AppController.addEssential;
import static my.loic.utilities.db.spring.DatabaseService.dbService;

@CrossOrigin(origins = "*")
@Controller
public class DonationController {

    private final EmailService emailService;

    public DonationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/donate/{reqId}")
    public String donatePage(Model model, Principal loggedUser, @PathVariable Long reqId) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByEmail(loggedUser.getName());
        addEssential(model, loggedUser, U);

        // the request to which we are donating
        Donation_Request req = dbService.getById(Donation_Request.class, reqId).orElseThrow();

        Donation D = new Donation();
        D.items = new ArrayList<>();
        model.addAttribute("req", req);
        model.addAttribute("donation", D);
        return "donate";
    }
    @PostMapping("/donate/{reqId}")
    public String doDonation(Model model, Principal loggedUser, @PathVariable Long reqId, @ModelAttribute Donation donation, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByEmail(loggedUser.getName());
        addEssential(model, loggedUser, U);
        Donation_Request req = dbService.getById(Donation_Request.class, reqId).orElseThrow();
        List<Donation_Item> items = donation.getItems();

        donation.CreatedAt = LocalDateTime.now();
        donation.UpdatedAt = LocalDateTime.now();
        donation.DonorID = U.getID();
        donation.ReceiverID = req.UserID;
        donation.Approved = false;

        donation = donation.WriteThenReturn().orElse(null);
        if (donation != null) {
            for (Donation_Item item : items) {
                item.DonationID = donation.ID;
                if (item.Write() == 0) {
                    donation.Delete();
                    redirectAttributes.addFlashAttribute("failed", "Failed to send a donation. There was an issue with an item.");
                    return "redirect:/donate/" + reqId;
                }
            }
            redirectAttributes.addFlashAttribute("successDon", "Successfully made a donation. Once it's approved you will receive an email or phone call to come fetch the items.");
            return "redirect:/home?page=0";
        } else {
            redirectAttributes.addFlashAttribute("failed", "Failed to send a donation. Try again later.");
            return "redirect:/donate/" + reqId;
        }
    }


    @PostMapping("/admin/donation/validate/{id}/accept")
    public String acceptDonation(Model model, Principal loggedUser, @PathVariable Long id, @RequestParam String message, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation donation = dbService.getById(Donation.class, id).orElseThrow();
        donation.UpdatedAt = LocalDateTime.now();
        donation.Approved = true;
        donation.UpdateOnly("Approved", "UpdatedAt");

        User donor = donation.getDonor();
        emailService.acceptDonation(donor.getEmail(), donor.getFirstName() + " " + donor.getLastName(), message);
        new Notification(donor.getID(), "Donation Approved", "Congratulations, your donation has been approved !");

        redirectAttributes.addFlashAttribute("successDon", "Successfully accepted the donation from " + donor.getFirstName() + ".");
        return "redirect:/admin?page=0";
    }
    @PostMapping("/admin/donation/validate/{id}/deny")
    public String denyDonation(Model model, Principal loggedUser, @PathVariable Long id, @RequestParam String message, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Donation donation = dbService.getById(Donation.class, id).orElseThrow();
        donation.Delete();

        User donor = donation.getDonor();
        emailService.denyDonation(donor.getEmail(), donor.getFirstName() + " " + donor.getLastName(), message);
        new Notification(donor.getID(), "Donation Denied", "Unfortunately your donation has been denied. More details sent by email.");

        redirectAttributes.addFlashAttribute("successDon", "Successfully denied the donation from " + donor.getFirstName() + ".");
        return "redirect:/admin?page=0";
    }
}
