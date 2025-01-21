package ru.isupden.vpnbot.vpn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.isupden.vpnbot.db.entity.PromoCode;
import ru.isupden.vpnbot.db.entity.User;
import ru.isupden.vpnbot.db.repo.PromoCodeRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromoCodeServiceTest {

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @InjectMocks
    private PromoCodeService promoCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPromoCodes_returnsValidPromoCodes() {
        PromoCode promoCode1 = new PromoCode("PROMO1", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().plusDays(1), new User());
        PromoCode promoCode2 = new PromoCode("PROMO2", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().plusDays(2), new User());
        when(promoCodeRepository.findAllByAssignedUserTelegramId(anyLong())).thenReturn(List.of(promoCode1, promoCode2));

        List<String> promoCodes = promoCodeService.getPromoCodes(123L);

        assertEquals(2, promoCodes.size());
        assertTrue(promoCodes.contains("PROMO1"));
        assertTrue(promoCodes.contains("PROMO2"));
    }

    @Test
    void getPromoCodes_excludesExpiredPromoCodes() {
        PromoCode promoCode1 = new PromoCode("PROMO1", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().minusDays(1), new User());
        PromoCode promoCode2 = new PromoCode("PROMO2", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().plusDays(2), new User());
        when(promoCodeRepository.findAllByAssignedUserTelegramId(anyLong())).thenReturn(List.of(promoCode1, promoCode2));

        List<String> promoCodes = promoCodeService.getPromoCodes(123L);

        assertEquals(1, promoCodes.size());
        assertTrue(promoCodes.contains("PROMO2"));
    }

    @Test
    void activatePromoCode_activatesValidPromoCode() {
        User user = new User();
        user.setTelegramId(123L);
        PromoCode promoCode = new PromoCode("PROMO1", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().plusDays(1), user);
        when(promoCodeRepository.findByPromoCode(anyString())).thenReturn(promoCode);

        promoCodeService.activatePromoCode("PROMO1", 123L);

        assertEquals(promoCode, user.getActivatedPromoCode());
        verify(promoCodeRepository, times(1)).save(promoCode);
    }

    @Test
    void activatePromoCode_doesNotActivateExpiredPromoCode() {
        User user = new User();
        user.setTelegramId(123L);
        PromoCode promoCode = new PromoCode("PROMO1", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().minusDays(1), user);
        when(promoCodeRepository.findByPromoCode(anyString())).thenReturn(promoCode);

        promoCodeService.activatePromoCode("PROMO1", 123L);

        assertNull(user.getActivatedPromoCode());
        verify(promoCodeRepository, never()).save(promoCode);
    }

    @Test
    void activatePromoCode_doesNotActivatePromoCodeForDifferentUser() {
        User user = new User();
        user.setTelegramId(123L);
        PromoCode promoCode = new PromoCode("PROMO1", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().plusDays(1), user);
        when(promoCodeRepository.findByPromoCode(anyString())).thenReturn(promoCode);

        promoCodeService.activatePromoCode("PROMO1", 456L);

        assertNull(user.getActivatedPromoCode());
        verify(promoCodeRepository, never()).save(promoCode);
    }

    @Test
    void activatePromoCode_doesNotActivateIfUserAlreadyHasActivatedPromoCode() {
        User user = new User();
        user.setTelegramId(123L);
        PromoCode existingPromoCode = new PromoCode("EXISTING", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().plusDays(1), user);
        user.setActivatedPromoCode(existingPromoCode);
        PromoCode newPromoCode = new PromoCode("PROMO1", 0.5, BigDecimal.valueOf(1), LocalDateTime.now().plusDays(1), user);
        when(promoCodeRepository.findByPromoCode(anyString())).thenReturn(newPromoCode);

        promoCodeService.activatePromoCode("PROMO1", 123L);

        assertEquals(existingPromoCode, user.getActivatedPromoCode());
        verify(promoCodeRepository, never()).save(newPromoCode);
    }
}