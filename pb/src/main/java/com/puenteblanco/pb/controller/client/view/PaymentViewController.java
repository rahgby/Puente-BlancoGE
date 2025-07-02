package com.puenteblanco.pb.controller.client.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentViewController {

    @GetMapping("/payment-form")
    public String showPaymentForm(Model model) {
        model.addAttribute("dashboard", null); // opcional, para que no falle si el HTML lo espera
        return "payment_form"; // ubicado en src/main/resources/templates
    }
}
