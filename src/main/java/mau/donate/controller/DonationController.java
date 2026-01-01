package mau.donate.controller;

import mau.donate.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@Controller
public class DonationController {

    private final EmailService emailService;

    public DonationController(EmailService emailService) {
        this.emailService = emailService;
    }


}
