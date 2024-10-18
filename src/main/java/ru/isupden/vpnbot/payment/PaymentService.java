package ru.isupden.vpnbot.payment;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.isupden.vpnbot.db.entity.PaymentEntity;
import ru.isupden.vpnbot.db.entity.PromoCode;
import ru.isupden.vpnbot.db.repo.PaymentRepository;
import ru.isupden.vpnbot.db.repo.PromoCodeRepository;
import ru.isupden.vpnbot.db.repo.UserRepository;
import ru.isupden.vpnbot.payment.dto.Amount;
import ru.isupden.vpnbot.payment.dto.ConfirmationRequest;
import ru.isupden.vpnbot.payment.dto.CreatePaymentRequest;
import ru.isupden.vpnbot.payment.dto.Notification;
import ru.isupden.vpnbot.vpn.VpnService;

import static ru.isupden.vpnbot.util.PromoCodeGenerator.generatePromoCode;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentClient paymentClient;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    @Autowired
    private VpnService vpnService;
    @Value("${bot.link}")
    private String link;

    public String createPayment(Long telegramId, Integer countOfMonths, Double multiplier) {
        var user = userRepository.findByTelegramId(telegramId);
        var payment = paymentRepository.save(new PaymentEntity(user, user.getPrice() * multiplier, countOfMonths));
        var paymentResponse = paymentClient.createPayment(new CreatePaymentRequest(
                new Amount(String.valueOf(payment.getAmount()), "RUB"),
                "Order: %s".formatted(payment.getId()),
                Map.of("telegramId", String.valueOf(telegramId)),
                true,
                new ConfirmationRequest("redirect", link)
        ), LocalDateTime.now().toString());
        payment.setPaymentId(paymentResponse.id());
        paymentRepository.save(payment);
        return paymentResponse.confirmation().confirmationUrl();
    }

    public void cancelPayment(Notification notification) {
        if (!notification.object().paid() || !notification.object().status().equals("succeeded")) {
            return;
        }
        var payment = paymentRepository.findByPaymentId(notification.object().id());
        payment.setDate(LocalDateTime.now());
        paymentRepository.save(payment);
        var user = payment.getUser();
        if (user.getAccessUrl() == null) {
            var referral = user.getReferral();
            if (referral != null) {
                var promoCode = generatePromoCode();
                while (promoCodeRepository.existsByPromoCode(promoCode)) {
                    promoCode = generatePromoCode();
                }
                //TODO сделать скидку на период
                promoCodeRepository.save(new PromoCode(promoCode, 50, LocalDateTime.now().plusMonths(1), referral));
            }
            user.setExpirationDate(LocalDateTime.now().plusMonths(payment.getNumberOfMonths()));
            user.setAccessUrl(vpnService.generateCode(user.getTelegramId()));
        } else {
            user.setExpirationDate(user.getExpirationDate().plusMonths(payment.getNumberOfMonths()));
        }
        //TODO константа
        user.setPrice(100d);
        userRepository.save(user);
    }
}
