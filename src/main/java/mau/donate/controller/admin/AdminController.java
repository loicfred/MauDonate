package mau.donate.controller.admin;

import mau.donate.objects.Campaign;
import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import mau.donate.service.CacheService;
import mau.donate.service.DatabaseObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static mau.donate.controller.AppController.addEssential;
import static mau.donate.controller.admin.DBEditorController.DBObjectPackage;

@CrossOrigin(origins = "*")
@Controller
public class AdminController {

    private final CacheService cacheService;

    public AdminController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @RequestMapping("/admin")
    public String admin(Model model, Principal loggedUser) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        model.addAttribute("dbstat", cacheService.getDatabaseStats());
        model.addAttribute("unapproved_reqs", Donation_Request.getAllWhere(Donation_Request.class, "NOT Approved"));

        return "admin";
    }

    @ResponseBody
    @GetMapping("/admin/list/{item}")
    public List<?> fetchItemList(Model model, Principal loggedUser, @PathVariable String item) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return null;
        addEssential(model, loggedUser, U);
        try {
            item = item.substring(0, 1).toUpperCase() + item.substring(1);
            Class<?> objClass = Class.forName(DBObjectPackage + item);
            return DatabaseObject.getAll(objClass);
        } catch (Exception e) {
            return null;
        }
    }
}
