package mau.donate.controller.admin.donate;

import mau.donate.objects.Donation;
import mau.donate.objects.Donation_Item;
import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import mau.donate.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

import static mau.donate.controller.AppController.addEssential;

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

        Donation_Request req = Donation_Request.getById(Donation_Request.class, reqId).orElseThrow();

        model.addAttribute("req", req);
        model.addAttribute("donation", new Donation());
        return "donate";
    }
    @PostMapping("/donate/{reqId}")
    public String doDonation(Model model, Principal loggedUser, @PathVariable Long reqId, @ModelAttribute Donation donation, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByEmail(loggedUser.getName());
        addEssential(model, loggedUser, U);

        Donation_Request req = Donation_Request.getById(Donation_Request.class, reqId).orElseThrow();

        donation.CreatedAt = LocalDateTime.now();
        donation.UpdatedAt = LocalDateTime.now();
        donation.DonorID = U.getID();
        donation.ReceiverID = req.UserID;
        donation.Approved = false;

        donation = donation.WriteThenReturn().orElse(null);
        if (donation != null) {
            for (Donation_Item item : donation.Items) {
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
}
