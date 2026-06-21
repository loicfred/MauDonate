package mau.donate.controller.admin;

import mau.donate.objects.Donation_Request;
import mau.donate.objects.derived.D_Donation;
import mau.donate.objects.derived.D_Warehouse;
import org.solarframework.web.auth.obj.Account_Notification;
import org.solarframework.web.auth.obj.Account_User;
import org.solarframework.web.auth.spring.AuthEmailService;
import org.solarframework.db.api.dto.Row;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

import static org.solarframework.db.spring.DatabaseRegistry.SolarDBManager;
import static org.solarframework.web.auth.spring.Constants.addEssential;

@CrossOrigin(origins = "*")
@Controller
public class AdminController {

    private final AuthEmailService emailService;

    public AdminController(AuthEmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal loggedUser) {
        if (loggedUser == null) return "redirect:/auth/v1/login";
        Account_User U = Account_User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        model.addAttribute("pending_dons", D_Donation.getNotBroughtDonations());
        model.addAttribute("unapproved_reqs", SolarDBManager.getAllWhere(Donation_Request.class, "NOT Approved AND NOT Completed"));
        model.addAttribute("warehouses", SolarDBManager.getAll(D_Warehouse.class));

        model.addAttribute("dbstat", SolarDBManager.getAllDatabaseStats());
        model.addAttribute("tstats", SolarDBManager.getDefaultService().doQuery("call maudonate.TotalStat();").orElseGet(() -> new Row(Map.of())));
        LocalDate LD = LocalDate.now();
        model.addAttribute("mstats", SolarDBManager.getDefaultService().doQuery("call maudonate.MonthlyStat(?,?);", LD.getYear(), LD.getMonthValue()).orElseGet(() -> new Row(Map.of())));

        return "admin/admin";
    }

    @PostMapping("/admin/email/send")
    public String sendEmail(Model model, Principal loggedUser, @RequestParam String email, @RequestParam String subject, @RequestParam String message, RedirectAttributes redirectAttributes) {
        if (loggedUser == null) return "redirect:/auth/v1/login";
        Account_User U = Account_User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return "redirect:/home";
        addEssential(model, loggedUser, U);

        Account_User receiver = Account_User.getByEmail(email);
        emailService.sendEmail(receiver.getEmail(), subject, message);
        new Account_Notification(receiver.getID(), subject, message);

        redirectAttributes.addFlashAttribute("successDb", "Successfully sent an email to " + receiver.getFirstName() + ".");
        return "redirect:/admin?page=3";
    }

    @ResponseBody
    @GetMapping("/admin/stats/{year}/{month}")
    public Map<String, Object> fetchItemList(Model model, Principal loggedUser, @PathVariable Long year, @PathVariable Long month) {
        if (loggedUser == null) return null;
        Account_User U = Account_User.getByAuthentication(loggedUser);
        if (!U.getRole().equals("ADMIN")) return null;
        addEssential(model, loggedUser, U);
        try {
            return SolarDBManager.getDefaultService().doQuery("call maudonate.MonthlyStat(?,?);", year, month).orElseThrow().getColumns();
        } catch (Exception e) {
            return null;
        }
    }
}
