package ru.isupden.vpnbot.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.isupden.vpnbot.payment.PaymentService;
import ru.isupden.vpnbot.payment.dto.Notification;

@RestController()
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment")
    public void update(@RequestBody Notification notification) {
        paymentService.cancelPayment(notification);
    }
}
