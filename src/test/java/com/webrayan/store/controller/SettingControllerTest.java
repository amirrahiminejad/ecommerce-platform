package com.webrayan.store.controller;

import com.webrayan.store.core.common.controller.SettingController;
import com.webrayan.store.core.common.entity.Setting;
import com.webrayan.store.core.common.service.SettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SettingControllerTest {

    @InjectMocks
    private SettingController settingController;

    @Mock
    private SettingService settingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSettingsByUserId() {
        Long userId = 1L;
        Setting setting1 = new Setting();
        Setting setting2 = new Setting();
        List<Setting> settings = Arrays.asList(setting1, setting2);

        when(settingService.getSettingsByUserId(userId)).thenReturn(settings);

        List<Setting> result = settingController.getSettingsByUserId(userId);

        assertEquals(2, result.size());
        verify(settingService, times(1)).getSettingsByUserId(userId);
    }

    @Test
    void testGetSettingByKeyAndUserId() {
        Long userId = 1L;
        String key = "someKey";
        Setting setting = new Setting();

        when(settingService.getSettingByKeyAndUserId(key, userId)).thenReturn(Optional.of(setting));

        Setting result = settingController.getSettingByKeyAndUserId(userId, key);

        assertNotNull(result);
        verify(settingService, times(1)).getSettingByKeyAndUserId(key, userId);
    }

    @Test
    void testGetSettingByKeyAndUserId_NotFound() {
        Long userId = 1L;
        String key = "someKey";

        when(settingService.getSettingByKeyAndUserId(key, userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            settingController.getSettingByKeyAndUserId(userId, key);
        });

        assertEquals("Setting not found.", exception.getMessage());
    }

    @Test
    void testSaveSetting() {
        Setting setting = new Setting();
        when(settingService.saveSetting(any(Setting.class))).thenReturn(setting);

        Setting result = settingController.saveSetting(setting);

        assertNotNull(result);
        verify(settingService, times(1)).saveSetting(setting);
    }

    @Test
    void testDeleteSetting() {
        Long id = 1L;

        settingController.deleteSetting(id);

        verify(settingService, times(1)).deleteSetting(id);
    }
}
