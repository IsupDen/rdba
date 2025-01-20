package ru.isupden.vpnbot.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.isupden.vpnbot.db.entity.PaymentEntity;
import ru.isupden.vpnbot.db.entity.PromoCode;
import ru.isupden.vpnbot.db.repo.PaymentRepository;
import ru.isupden.vpnbot.db.repo.PromoCodeRepository;
import ru.isupden.vpnbot.db.repo.SubscriptionRepository;
import ru.isupden.vpnbot.db.repo.UserRepository;
import ru.isupden.vpnbot.payment.dto.Amount;
import ru.isupden.vpnbot.payment.dto.ConfirmationRequest;
import ru.isupden.vpnbot.payment.dto.CreatePaymentRequest;
import ru.isupden.vpnbot.payment.dto.Notification;
import ru.isupden.vpnbot.vpn.VpnService;

import static ru.isupden.vpnbot.util.PromoCodeGenerator.generatePromoCode;

@Slf4j
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
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private VpnService vpnService;
    @Value("${bot.link}")
    private String link;

    public String createPayment(Long telegramId, UUID subscriptionId, BigDecimal price) {
        var user = userRepository.findByTelegramId(telegramId);
        var subscription = subscriptionRepository.getReferenceById(subscriptionId);
        var payment = paymentRepository.save(new PaymentEntity(user, price, subscription));
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
        //TODO обработать
        if (!notification.object().paid() || !notification.object().status().equals("succeeded")) {
            return;
        }
        var payment = paymentRepository.findByPaymentId(notification.object().id());
        payment.setDate(LocalDateTime.now());
        paymentRepository.save(payment);
        log.info("Payment cancelled: {}", payment);
        var user = payment.getUser();
        if (user.getVpnConfiguration() == null || user.getVpnConfiguration().getAccessUrl() == null) {
            var referral = user.getReferral();
            if (referral != null) {
                var promoCode = generatePromoCode();
                while (promoCodeRepository.existsByPromoCode(promoCode)) {
                    promoCode = generatePromoCode();
                }
                //TODO вынести генерацию
                promoCodeRepository.save(new PromoCode(promoCode, 0.5, payment.getSubscriptionPlan().getPrice().multiply(BigDecimal.valueOf(0.5)), LocalDateTime.now().plusMonths(1), referral));
            }
            user.setSubscriptionEndDate(LocalDateTime.now().plusMonths(payment.getSubscriptionPlan().getDurationMonths()));
            //TODO криво
            user = vpnService.generateCode(user);
        } else {
            user.setSubscriptionEndDate(user.getSubscriptionEndDate().plusMonths(payment.getSubscriptionPlan().getDurationMonths()));
        }
        userRepository.save(user);
    }
}
