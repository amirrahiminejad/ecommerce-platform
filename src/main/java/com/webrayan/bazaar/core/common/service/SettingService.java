package com.webrayan.bazaar.core.common.service;

import com.webrayan.bazaar.core.common.entity.Setting;
import com.webrayan.bazaar.core.common.repository.SettingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SettingService {

    private final SettingRepository repository;

    public SettingService(SettingRepository repository) {
        this.repository = repository;
    }

    public List<Setting> getSettingsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public Optional<Setting> getSettingByKeyAndUserId(String key, Long userId) {
        return Optional.ofNullable(repository.findByKeyAndUserId(key, userId));
    }

    public Setting saveSetting(Setting setting) {
        if (setting.getKey() == null || setting.getKey().trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty.");
        }
        if (setting.getValue() == null || setting.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty.");
        }
        return repository.save(setting);
    }

    public void deleteSetting(Long id) {
        repository.deleteById(id);
    }
}
