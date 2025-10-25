package com.webrayan.bazaar.service;

import com.webrayan.bazaar.core.common.entity.Setting;
import com.webrayan.bazaar.modules.acl.entity.User;
import com.webrayan.bazaar.core.common.repository.SettingRepository;
import com.webrayan.bazaar.core.common.service.SettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingServiceTest {

    @InjectMocks
    private SettingService settingService;

    @Mock
    private SettingRepository settingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void getSettingsByUserId_ShouldReturnSettingsList() {
        User user = new User();
        user.setId(10l);

        Setting s1 = new Setting();
        s1.setId(1L);
        s1.setUser(user);
        s1.setKey("theme");
        s1.setValue("dark");

        Setting s2 = new Setting();
        s2.setId(2L);
        s2.setUser(user);
        s2.setKey("language");
        s2.setValue("fa");

        when(settingRepository.findByUserId(10L)).thenReturn(List.of(s1, s2));

        List<Setting> result = settingService.getSettingsByUserId(10L);

        assertEquals(2, result.size());
        assertEquals(user, result.get(0).getUser());
        verify(settingRepository, times(1)).findByUserId(10L);
    }

    @Test
    void getSettingByKeyAndUserId_ShouldReturnSetting_WhenExists() {
        User user = new User();
        user.setId(10l);

        Setting s = new Setting();
        s.setId(1L);
        s.setUser(user);
        s.setKey("theme");
        s.setValue("dark");

        when(settingRepository.findByKeyAndUserId("theme", 10L)).thenReturn(s);

        Optional<Setting> result = settingService.getSettingByKeyAndUserId("theme", 10L);

        assertTrue(result.isPresent());
        assertEquals("dark", result.get().getValue());
        assertEquals(user, result.get().getUser());
        verify(settingRepository, times(1)).findByKeyAndUserId("theme", 10L);
    }

    @Test
    void saveSetting_ShouldSaveAndReturnSetting_WhenValid() {
        User user = new User();
        user.setId(10l);

        Setting setting = new Setting();
        setting.setUser(user);
        setting.setKey("theme");
        setting.setValue("dark");

        when(settingRepository.save(setting)).thenReturn(setting);

        Setting saved = settingService.saveSetting(setting);

        assertNotNull(saved);
        assertEquals("theme", saved.getKey());
        assertEquals("dark", saved.getValue());
        assertEquals(user, saved.getUser());
        verify(settingRepository, times(1)).save(setting);
    }
}
