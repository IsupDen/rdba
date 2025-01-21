package ru.isupden.vpnbot.vpn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.isupden.vpnbot.db.entity.User;
import ru.isupden.vpnbot.db.entity.VpnConfiguration;
import ru.isupden.vpnbot.db.repo.UserRepository;
import ru.isupden.vpnbot.vpn.dto.AccessKey;
import ru.isupden.vpnbot.vpn.outline.OutlineClient;
import ru.isupden.vpnbot.vpn.outline.dto.CreateAccessKeyRequest;
import ru.isupden.vpnbot.vpn.outline.dto.CreateAccessKeyResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VpnServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OutlineClient outlineClient;

    @InjectMocks
    private VpnService vpnService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_createsNewUserWhenNotExists() {
        when(userRepository.existsByTelegramId(anyLong())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        vpnService.createUser(123L, "456", "Test User");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_doesNotCreateUserWhenExists() {
        when(userRepository.existsByTelegramId(anyLong())).thenReturn(true);

        vpnService.createUser(123L, "456", "Test User");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void generateCode_createsAccessKeyWhenNotExists() {
        User user = new User("Test User", 123L, null);
        when(outlineClient.createUser(any(CreateAccessKeyRequest.class))).thenReturn(new CreateAccessKeyResponse("id", "name","password", 1234, "method", "url"));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = vpnService.generateCode(user);

        assertNotNull(result.getVpnConfiguration());
        verify(outlineClient, times(1)).createUser(any(CreateAccessKeyRequest.class));
    }

    @Test
    void generateCode_doesNotCreateAccessKeyWhenExists() {
        User user = new User("Test User", 123L, null);
        user.setVpnConfiguration(new VpnConfiguration("url", "password", "method", 1234, "server", "id"));

        User result = vpnService.generateCode(user);

        assertNotNull(result.getVpnConfiguration());
        verify(outlineClient, never()).createUser(any(CreateAccessKeyRequest.class));
    }

    @Test
    void getLink_returnsLinkWhenUserExists() {
        User user = new User("Test User", 123L, null);
        user.setVpnConfiguration(new VpnConfiguration("url", "password", "method", 1234, "server", "id"));
        when(userRepository.existsByTelegramId(anyLong())).thenReturn(true);
        when(userRepository.findByTelegramId(anyLong())).thenReturn(user);

        String link = vpnService.getLink(123L);

        assertNotNull(link);
    }

    @Test
    void getLink_returnsNullWhenUserNotExists() {
        when(userRepository.existsByTelegramId(anyLong())).thenReturn(false);

        String link = vpnService.getLink(123L);

        assertNull(link);
    }

    @Test
    void getDays_returnsDaysWhenUserExists() {
        User user = new User("Test User", 123L, null);
        user.setSubscriptionEndDate(LocalDateTime.now().plusDays(10));
        when(userRepository.existsByTelegramId(anyLong())).thenReturn(true);
        when(userRepository.findByTelegramId(anyLong())).thenReturn(user);

        Long days = vpnService.getDays(123L);

        assertEquals(10, days);
    }

    @Test
    void getDays_returnsNullWhenUserNotExists() {
        when(userRepository.existsByTelegramId(anyLong())).thenReturn(false);

        Long days = vpnService.getDays(123L);

        assertNull(days);
    }

    @Test
    void getKey_returnsAccessKeyWhenValid() {
        User user = new User("Test User", 123L, null);
        user.setVpnConfiguration(new VpnConfiguration("url", "password", "method", 1234, "server", "id"));
        user.setSubscriptionEndDate(LocalDateTime.now().plusDays(10));
        when(userRepository.findByVpnConfigurationPassword(anyString())).thenReturn(user);

        AccessKey accessKey = vpnService.getKey("password");

        assertNotNull(accessKey);
    }

    @Test
    void getKey_returnsNullWhenInvalid() {
        when(userRepository.findByVpnConfigurationPassword(anyString())).thenReturn(null);

        AccessKey accessKey = vpnService.getKey("password");

        assertNull(accessKey);
    }
}