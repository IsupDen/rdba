package ru.isupden.vpnbot.vpn.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.isupden.vpnbot.vpn.VpnService;
import ru.isupden.vpnbot.vpn.dto.AccessKey;

@Slf4j
@RestController
public class AccessKeyController {
    @Autowired
    private VpnService vpnService;

    @GetMapping("/conf/{password}")
    public AccessKey getKey(@PathVariable String password) {
        log.info("getting access key for {}", password);
        var accessKey = vpnService.getKey(password);
        log.info("access key: {}", accessKey);
        return accessKey;
    }
}
