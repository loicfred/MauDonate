package mau.donate.controller.admin;

import mau.donate.objects.Donation_Item;
import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import mau.donate.objects.Warehouse;
import mau.donate.objects.derived.D_Warehouse;
import mau.donate.objects.enums.StorageStatus;
import mau.donate.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Map;

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

        D_Warehouse WH = D_Warehouse.getById(D_Warehouse.class, id).orElse(null);
        model.addAttribute("warehouse", WH);
        return "warehouse";
    }

    @PostMapping("/admin/warehouse/{warehouseId}/items/update")
    public String updateItemStatus(Principal loggedUser, @PathVariable Long warehouseId, @ModelAttribute Warehouse warehouse, RedirectAttributes redirectAttributes) {
        try {
            User U = User.getByAuthentication(loggedUser);
            if (!U.getRole().equals("ADMIN")) throw new Exception("Unauthorized");
            for (Donation_Item I : warehouse.getItems()) I.UpdateOnly("Status");

            redirectAttributes.addFlashAttribute("success", "Successfully updated the warehouse.");
            return "redirect:/admin/warehouse/" + warehouseId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/admin/warehouse/" + warehouseId;
        }
    }
}