package mau.donate.controller;

import mau.donate.objects.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@Controller
public class AppController {

    //@Cacheable(key = "1", value = "index")
    @RequestMapping("/")
    public String index(Model model, Principal loggedUser) {
        addEssential(model, loggedUser);
        model.addAttribute("requests", Donation_Request.getAllWhere(Donation_Request.class, "isApproved AND NOT isCompleted"));
        model.addAttribute("campaigns", Campaign.getAll(Campaign.class));
        return "index";
    }

    @RequestMapping("/home")
    public String home(Model model, Principal loggedUser) {
        return index(model, loggedUser);
    }
    @RequestMapping("/donate")
    public String donation(Model model, Principal loggedUser) {
        addEssential(model, loggedUser);
        return "donate";
    }

    private void addEssential(Model model, Principal loggedUser) {
        model.addAttribute("principal", loggedUser);
        if (loggedUser != null) {
            User U = User.getByUsername(loggedUser.getName());
            List<Notification> notifs = Notification.ofUser(U.getId(), 7);
            model.addAttribute("notifications", notifs);
            model.addAttribute("isRead", notifs.stream().allMatch(Notification::isRead));
        } else {
            model.addAttribute("notifications", new ArrayList<>());
            model.addAttribute("isRead", true);
        }
    }

}
