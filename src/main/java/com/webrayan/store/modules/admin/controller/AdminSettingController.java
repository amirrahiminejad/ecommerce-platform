package com.webrayan.store.modules.admin.controller;

import com.webrayan.store.core.common.entity.Setting;
import com.webrayan.store.core.common.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
@Slf4j
public class AdminSettingController {

    private final SettingRepository settingRepository;

    @GetMapping
    public String listSettings(Model model) {
        log.info("Admin accessing settings list");
        
        // Get system-wide settings (where user is null)
        List<Setting> settings = settingRepository.findAll(Sort.by(Sort.Direction.ASC, "key"))
                .stream()
                .filter(setting -> setting.getUser() == null)  // Only system settings
                .toList();
        
        model.addAttribute("settings", settings);
        return "admin/pages/settings";
    }

    @PostMapping("/update")
    public String updateSettings(@RequestParam("keys") List<String> keys,
                               @RequestParam("values") List<String> values,
                               RedirectAttributes redirectAttributes) {
        
        log.info("Admin updating settings, count: {}", keys.size());
        
        try {
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String value = values.get(i);
                
                Optional<Setting> existingSetting = settingRepository.findByKey(key);
                if (existingSetting.isPresent()) {
                    Setting setting = existingSetting.get();
                    // Only update system-wide settings (where user is null)
                    if (setting.getUser() == null) {
                        setting.setValue(value);
                        settingRepository.save(setting);
                        log.debug("Updated setting: {} = {}", key, value);
                    }
                }
            }
            
            log.info("Settings updated successfully");
            redirectAttributes.addFlashAttribute("successMessage", "تنظیمات با موفقیت بروزرسانی شد");
            
        } catch (Exception e) {
            log.error("Error updating settings", e);
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در بروزرسانی تنظیمات: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }

    @PostMapping("/reset")
    public String resetSettings(RedirectAttributes redirectAttributes) {
        log.info("Admin resetting settings to defaults");
        
        try {
            // Reset to default values
            resetToDefaults();
            
            log.info("Settings reset successfully");
            redirectAttributes.addFlashAttribute("successMessage", "تنظیمات با موفقیت به حالت پیش‌فرض بازگردانده شد");
            
        } catch (Exception e) {
            log.error("Error resetting settings", e);
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در بازگردانی تنظیمات: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }

    @PostMapping("/add")
    public String addSetting(@RequestParam String key,
                           @RequestParam String value,
                           RedirectAttributes redirectAttributes) {
        
        log.info("Admin adding new setting: {} = {}", key, value);
        
        try {
            if (settingRepository.existsByKey(key)) {
                redirectAttributes.addFlashAttribute("errorMessage", "کلید تنظیمات قبلاً موجود است");
                return "redirect:/admin/settings";
            }
            
            Setting setting = new Setting();
            setting.setKey(key);
            setting.setValue(value);
            setting.setUser(null); // System-wide setting
            settingRepository.save(setting);
            
            log.info("New setting added successfully");
            redirectAttributes.addFlashAttribute("successMessage", "تنظیمات جدید با موفقیت اضافه شد");
            
        } catch (Exception e) {
            log.error("Error adding new setting", e);
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در افزودن تنظیمات: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }

    @PostMapping("/delete/{id}")
    public String deleteSetting(@PathVariable Long id,
                              RedirectAttributes redirectAttributes) {
        
        log.info("Admin deleting setting with id: {}", id);
        
        try {
            Optional<Setting> setting = settingRepository.findById(id);
            if (setting.isPresent()) {
                settingRepository.deleteById(id);
                log.info("Setting deleted successfully: {}", setting.get().getKey());
                redirectAttributes.addFlashAttribute("successMessage", "تنظیمات با موفقیت حذف شد");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "تنظیمات یافت نشد");
            }
            
        } catch (Exception e) {
            log.error("Error deleting setting", e);
            redirectAttributes.addFlashAttribute("errorMessage", "خطا در حذف تنظیمات: " + e.getMessage());
        }
        
        return "redirect:/admin/settings";
    }

    private void resetToDefaults() {
        // Delete all existing settings
        settingRepository.deleteAll();
        
        // Recreate default settings
        String[][] defaultSettings = {
            {"site_name", "فروشگاه اینترنتی"},
            {"site_title", "فروشگاه آنلاین"},
            {"site_description", "فروشگاه اینترنتی با محصولات متنوع و کیفیت بالا"},
            {"default_currency", "تومان"},
            {"default_language", "fa"},
            {"admin_email", "admin@online-store.com"},
            {"support_phone", "+98-21-12345678"},
            {"max_upload_size", "10485760"},
            {"items_per_page", "20"},
            {"maintenance_mode", "false"}
        };
        
        for (String[] data : defaultSettings) {
            Setting setting = new Setting();
            setting.setKey(data[0]);
            setting.setValue(data[1]);
            setting.setUser(null); // System-wide setting
            settingRepository.save(setting);
        }
    }
}
