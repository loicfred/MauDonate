package mau.donate.controller;

import jakarta.servlet.http.HttpServletRequest;
import mau.donate.objects.*;
import mau.donate.objects.derived.D_Donation_Request;
import org.solarframework.web.auth.obj.Account_Notification;
import org.solarframework.web.auth.obj.Account_User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;
import static org.solarframework.web.auth.spring.Constants.addEssential;

@CrossOrigin(origins = "*")
@Controller
public class AppController {

    @GetMapping("/home")
    public String home(Model model, Principal loggedUser) {
        Account_User U = Account_User.getByAuthentication(loggedUser);
        if (loggedUser != null && U == null) return "redirect:/logout";
        System.out.println(U);
        System.out.println(U.getRole());
        System.out.println(loggedUser);
        addEssential(model, loggedUser, U);
        model.addAttribute("requests", SolarDBManager.getAllWhere(D_Donation_Request.class, "Approved AND NOT Completed ORDER BY Upvotes DESC"));
        model.addAttribute("campaigns", SolarDBManager.getAll(Campaign.class));
        return "index";
    }

    @GetMapping("/")
    public String index(Model model, Principal loggedUser) {
        return home(model, loggedUser);
    }

    @GetMapping("/fundraise")
    public String donation(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/auth/v1/login";
        Account_User U = Account_User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        return "fundraise";
    }

    @GetMapping("/billing")
    public String billing(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/auth/v1/login";
        Account_User U = Account_User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        model.addAttribute("fundraisings", SolarDBManager.getAllWhere(Fundraising.class, "DonorID = ? ", U.getID()));
        model.addAttribute("donations", SolarDBManager.getAllWhere(Donation.class, "DonorID = ? ", U.getID()));
        return "billing";
    }

    @GetMapping("/request")
    public String request(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/auth/v1/login";
        Account_User U = Account_User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        return "request";
    }

    @GetMapping("/offline")
    public String offline() {
        return "offline";
    }



    @GetMapping("/error")
    public String error(HttpServletRequest request, Principal loggedUser, Model model) {
        Account_User U = Account_User.getByAuthentication(loggedUser);
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
        Account_User U = Account_User.getByAuthentication(loggedUser);
        for (Account_Notification n : Account_Notification.ofUser(U.getID(), 100)) {
            if (!n.isOpened()) {
                n.setOpened(true);
                n.UpdateOnly("isRead");
            }
        }
        return true;
    }

    @GetMapping("/upvote/{requestId}/{onoff}")
    @ResponseBody
    public boolean upvote_request(Principal loggedUser, @PathVariable long requestId, @PathVariable long onoff) {
        if (loggedUser == null) return false;
        Account_User U = Account_User.getByAuthentication(loggedUser);
        Donation_Upvote upvote = SolarDBManager.getWhere(Donation_Upvote.class, "UserID = ? AND RequestID = ?", U.getID(), requestId).orElse(null);
        if (onoff == 0 && upvote != null) {
            upvote.Delete();
        } else if (onoff == 1 && upvote == null) {
            new Donation_Upvote(U.getID(), requestId);
        }
        SolarDBManager.resetCacheForClass(D_Donation_Request.class, true, true);
        return true;
    }


}
