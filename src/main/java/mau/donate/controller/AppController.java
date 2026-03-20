package mau.donate.controller;

import jakarta.servlet.http.HttpServletRequest;
import mau.donate.objects.*;
import mau.donate.objects.derived.D_Donation_Request;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static my.loic.utilities.db.spring.DatabaseService.dbService;

@CrossOrigin(origins = "*")
@Controller
public class AppController {

    @GetMapping("/home")
    public String home(Model model, Principal loggedUser) {
        User U = User.getByAuthentication(loggedUser);
        if (loggedUser != null && U == null) return "redirect:/logout";
        addEssential(model, loggedUser, U);
        model.addAttribute("requests", dbService.getAllWhere(D_Donation_Request.class, "Approved AND NOT Completed ORDER BY Upvotes DESC"));
        model.addAttribute("campaigns", dbService.getAll(Campaign.class));
        return "index";
    }

    @GetMapping("/")
    public String index(Model model, Principal loggedUser) {
        return home(model, loggedUser);
    }

    @GetMapping("/fundraise")
    public String donation(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        return "fundraise";
    }

    @GetMapping("/billing")
    public String billing(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        model.addAttribute("fundraisings", dbService.getAllWhere(Fundraising.class, "DonorID = ? ", U.getID()));
        model.addAttribute("donations", dbService.getAllWhere(Donation.class, "DonorID = ? ", U.getID()));
        return "billing";
    }

    @GetMapping("/request")
    public String request(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        return "request";
    }

    @GetMapping("/offline")
    public String offline() {
        return "offline";
    }



    @GetMapping("/error")
    public String error(HttpServletRequest request, Principal loggedUser, Model model) {
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String message = (throwable != null) ? throwable.getMessage() : "N/A";

        model.addAttribute("error", true);
        model.addAttribute("status", statusCode);
        model.addAttribute("message", message);
        return "error";
    }


    @GetMapping("/read-notifications")
    @ResponseBody
    public boolean read_notifications(Principal loggedUser) {
        if (loggedUser == null) return false;
        User U = User.getByAuthentication(loggedUser);
        for (Notification n : Notification.ofUser(U.getID(), 100)) {
            if (!n.isRead()) {
                n.setRead(true);
                n.UpdateOnly("isRead");
            }
        }
        return true;
    }

    @GetMapping("/upvote/{requestId}/{onoff}")
    @ResponseBody
    public boolean upvote_request(Principal loggedUser, @PathVariable long requestId, @PathVariable long onoff) {
        if (loggedUser == null) return false;
        User U = User.getByAuthentication(loggedUser);
        Donation_Upvote upvote = dbService.getWhere(Donation_Upvote.class, "UserID = ? AND RequestID = ?", U.getID(), requestId).orElse(null);
        if (onoff == 0 && upvote != null) {
            upvote.Delete();
        } else if (onoff == 1 && upvote == null) {
            new Donation_Upvote(U.getID(), requestId);
        }
        dbService.resetCacheForClass(D_Donation_Request.class, true, true);
        return true;
    }



    public static void addEssential(Model model, Principal loggedUser, User U) {
        model.addAttribute("principal", loggedUser);
        if (loggedUser != null) {
            model.addAttribute("user", U);
            List<Notification> notifs = Notification.ofUser(U.getID(), 7);
            model.addAttribute("notifications", notifs);
            model.addAttribute("isRead", notifs.stream().allMatch(Notification::isRead));
        } else {
            model.addAttribute("notifications", new ArrayList<>());
            model.addAttribute("isRead", true);
        }
    }

}
