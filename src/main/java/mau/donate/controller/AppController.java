package mau.donate.controller;

import jakarta.servlet.http.HttpServletRequest;
import mau.donate.objects.*;
import mau.donate.objects.derived.D_Warehouse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@Controller
public class AppController {

    @GetMapping("/home")
    public String home(Model model, Principal loggedUser) {
        User U = User.getByAuthentication(loggedUser);
        if (loggedUser != null && U == null) return "redirect:/logout";
        addEssential(model, loggedUser, U);
        model.addAttribute("requests", Donation_Request.getAllWhere(Donation_Request.class, "Approved AND NOT Completed"));
        model.addAttribute("campaigns", Campaign.getAll(Campaign.class));
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
        model.addAttribute("fundraisings", Fundraising.getAllWhere(Fundraising.class, "DonorID = ? ", U.getID()));
        model.addAttribute("donations", Donation.getAllWhere(Donation.class, "DonorID = ? ", U.getID()));
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
