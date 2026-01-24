package mau.donate.controller.admin;

import mau.donate.objects.Donation_Request;
import mau.donate.objects.User;
import mau.donate.objects.derived.D_Donation;
import mau.donate.objects.derived.D_Warehouse;
import mau.donate.service.database.DatabaseObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

import static mau.donate.config.AppConfig.dbService;
import static mau.donate.controller.AppController.addEssential;
import static mau.donate.controller.admin.DBEditorController.DBObjectPackage;

@CrossOrigin(origins = "*")
@Controller
public class AdminController {

    public AdminController() {}

    @GetMapping("/admin")
    public String admin(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/accounts/login";
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        model.addAttribute("pending_dons", D_Donation.getNotBroughtDonations());
        model.addAttribute("unapproved_reqs", Donation_Request.getAllWhere(Donation_Request.class, "NOT Approved AND NOT Completed"));
        model.addAttribute("warehouses", D_Warehouse.getAll(D_Warehouse.class));

        model.addAttribute("dbstat", dbService.getDatabaseStats());
        model.addAttribute("tstats", DatabaseObject.doQuery("call maudonate.TotalStat();").orElseGet(() -> new DatabaseObject.Row(Map.of())));
        LocalDate LD = LocalDate.now();
        model.addAttribute("mstats", DatabaseObject.doQuery("call maudonate.MonthlyStat(?,?);", LD.getYear(), LD.getMonthValue()).orElseGet(() -> new DatabaseObject.Row(Map.of())));

        return "admin/admin";
    }

    @ResponseBody
    @GetMapping("/admin/list/{item}")
    public Map<String, ?> fetchItemList(Model model, Principal loggedUser, @PathVariable String item) {
        if (loggedUser == null) return null;
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return null;
        addEssential(model, loggedUser, U);
        try {
            item = item.substring(0, 1).toUpperCase() + item.substring(1);
            Class<?> objClass = Class.forName(DBObjectPackage + item);
            return Map.of("tblstats", dbService.getTableStats(item), "items", DatabaseObject.getAll(objClass));
        } catch (Exception e) {
            return null;
        }
    }

    @ResponseBody
    @GetMapping("/admin/stats/{year}/{month}")
    public Map<String, Object> fetchItemList(Model model, Principal loggedUser, @PathVariable Long year, @PathVariable Long month) {
        if (loggedUser == null) return null;
        User U = User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return null;
        addEssential(model, loggedUser, U);
        try {
            return DatabaseObject.doQuery("call maudonate.MonthlyStat(?,?);", year, month).orElseThrow().columns;
        } catch (Exception e) {
            return null;
        }
    }
}
