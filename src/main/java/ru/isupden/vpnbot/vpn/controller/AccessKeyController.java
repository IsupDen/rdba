package ru.isupden.vpnbot.vpn.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.isupden.vpnbot.vpn.VpnService;
import ru.isupden.vpnbot.vpn.dto.AccessKey;

@RestController
public class AccessKeyController {
    @Autowired
    private VpnService vpnService;

    @GetMapping("/conf/{password}")
    public AccessKey getKey(@PathVariable String password) {
        return vpnService.getKey(password);
    }
}
