package com.webrayan.bazaar.core.common.controller;

import com.webrayan.bazaar.core.common.entity.Setting;
import com.webrayan.bazaar.core.common.service.SettingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingController {

    private final SettingService service;

    public SettingController(SettingService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public List<Setting> getSettingsByUserId(@PathVariable Long userId) {
        return service.getSettingsByUserId(userId);
    }

    @GetMapping("/user/{userId}/key/{key}")
    public Setting getSettingByKeyAndUserId(@PathVariable Long userId, @PathVariable String key) {
        return service.getSettingByKeyAndUserId(key, userId)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found."));
    }

    @PostMapping()
    public Setting saveSetting(@RequestBody Setting setting) {
        return service.saveSetting(setting);
    }

    @DeleteMapping("/{id}")
    public void deleteSetting(@PathVariable Long id) {
        service.deleteSetting(id);
    }
}
