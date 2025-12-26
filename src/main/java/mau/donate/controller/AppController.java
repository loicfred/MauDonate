package mau.donate.controller;

import mau.donate.objects.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@Controller
public class AppController {

    //@Cacheable(key = "1", value = "index")
    @RequestMapping("/home")
    public String index(Model model, Principal loggedUser) {
        User U = User.getByAuthentication(loggedUser);
        if (loggedUser != null && U == null) return "redirect:/logout";
        addEssential(model, loggedUser, U);
        model.addAttribute("requests", Donation_Request.getAllWhere(Donation_Request.class, "isApproved AND NOT isCompleted"));
        model.addAttribute("campaigns", Campaign.getAll(Campaign.class));
        return "index";
    }

    @RequestMapping("/donate")
    public String donation(Model model, Principal loggedUser) {
        User U = User.getByAuthentication(loggedUser);
        addEssential(model, loggedUser, U);
        return "donate";
    }



    @GetMapping("/offline")
    public String offline() {
        return "offline";
    }






    public static void addEssential(Model model, Principal loggedUser, User U) {
        model.addAttribute("principal", loggedUser);
        if (loggedUser != null) {
            List<Notification> notifs = Notification.ofUser(U.getID(), 7);
            model.addAttribute("notifications", notifs);
            model.addAttribute("isRead", notifs.stream().allMatch(Notification::isRead));
        } else {
            model.addAttribute("notifications", new ArrayList<>());
            model.addAttribute("isRead", true);
        }
    }

}
