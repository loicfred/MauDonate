package mau.donate.controller.admin;

import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import mau.donate.objects.Warehouse;
import mau.donate.objects.derived.D_Warehouse;
import mau.donate.service.CacheService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static mau.donate.controller.AppController.addEssential;

@CrossOrigin(origins = "*")
@Controller
public class WarehouseController {

    private final CacheService cacheService;

    public WarehouseController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @RequestMapping("/admin/warehouse/{id}")
    public String ViewWarehouse(Model model, Principal loggedUser, @PathVariable Long id) {
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Warehouse WH = D_Warehouse.getById(D_Warehouse.class, id).orElse(null);
        model.addAttribute("warehouse", WH);
        return "warehouse";
    }
}