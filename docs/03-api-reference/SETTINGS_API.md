# Settings API Documentation

## Overview
The Admin Settings API provides endpoints for managing system-wide configuration settings.

## Base URL
```
/admin/settings
```

## Authentication
- Requires admin role
- Session-based authentication

## Endpoints

### GET /admin/settings
Display all system settings

**Response:**
- **200 OK:** Returns settings page
- **403 Forbidden:** Insufficient permissions

**Template Variables:**
```java
Model attributes:
- List<Setting> settings - All system-wide settings (user = null)
```

### POST /admin/settings/update
Update multiple settings at once

**Parameters:**
```java
@RequestParam("keys") List<String> keys
@RequestParam("values") List<String> values
```

**Response:**
- **302 Redirect:** Back to settings page with success/error message

**Example Request:**
```
POST /admin/settings/update
Content-Type: application/x-www-form-urlencoded

keys=site_name&keys=admin_email&values=My Store&values=admin@mystore.com
```

### POST /admin/settings/add
Add a new setting

**Parameters:**
```java
@RequestParam String key
@RequestParam String value
```

**Validation:**
- Key must be unique
- Key and value are required

**Response:**
- **302 Redirect:** Back to settings page with success/error message

**Example Request:**
```
POST /admin/settings/add
Content-Type: application/x-www-form-urlencoded

key=new_setting&value=new_value
```

### POST /admin/settings/delete/{id}
Delete a setting by ID

**Path Parameters:**
- `id` (Long): Setting ID

**Response:**
- **302 Redirect:** Back to settings page with success/error message

**Example Request:**
```
POST /admin/settings/delete/123
```

### POST /admin/settings/reset
Reset all settings to default values

**Response:**
- **302 Redirect:** Back to settings page with success/error message

**Warning:** This operation is irreversible!

## Data Model

### Setting Entity
```java
@Entity
@Table(name = "settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String key;      // Setting key (unique)
    private String value;    // Setting value
    
    @ManyToOne
    private User user;       // null for system-wide settings
}
```

### Repository Interface
```java
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByKey(String key);
    boolean existsByKey(String key);
    List<Setting> findByUserId(Long userId);
    Setting findByKeyAndUserId(String key, Long userId);
}
```

## Default Settings

The system initializes with these default settings:

```java
{
    "site_name": "فروشگاه اینترنتی",
    "site_title": "فروشگاه آنلاین", 
    "site_description": "فروشگاه اینترنتی با محصولات متنوع و کیفیت بالا",
    "default_currency": "تومان",
    "default_language": "fa",
    "admin_email": "admin@online-store.com",
    "support_phone": "+98-21-12345678",
    "max_upload_size": "10485760",
    "items_per_page": "20",
    "maintenance_mode": "false"
}
```

## Error Handling

### Common Error Messages
```java
// Success messages
"تنظیمات با موفقیت بروزرسانی شد"
"تنظیمات جدید با موفقیت اضافه شد"
"تنظیمات با موفقیت حذف شد"
"تنظیمات با موفقیت به حالت پیش‌فرض بازگردانده شد"

// Error messages
"خطا در بروزرسانی تنظیمات: {error}"
"کلید تنظیمات قبلاً موجود است"
"تنظیمات یافت نشد"
"خطا در حذف تنظیمات: {error}"
"خطا در بازگردانی تنظیمات: {error}"
"خطا در افزودن تنظیمات: {error}"
```

## Security Considerations

### Access Control
- Only users with admin role can access these endpoints
- All operations are logged for audit purposes

### Data Validation
- Setting keys must be unique
- Email fields are validated for proper format
- Numeric fields are validated for valid numbers

### System Settings vs User Settings
The API filters settings to show only system-wide settings (where `user` is null).

## Usage Examples

### Frontend JavaScript
```javascript
// Delete setting
function deleteSetting(settingId, settingKey) {
    if (confirm('آیا از حذف تنظیمات "' + settingKey + '" اطمینان دارید؟')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/admin/settings/delete/' + settingId;
        document.body.appendChild(form);
        form.submit();
    }
}

// Reset settings
function resetSettings() {
    if (confirm('آیا از بازگردانی تمام تنظیمات به حالت پیش‌فرض اطمینان دارید؟')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/admin/settings/reset';
        document.body.appendChild(form);
        form.submit();
    }
}
```

### Service Layer Usage
```java
@Service
public class SettingsService {
    
    public String getSetting(String key) {
        return settingRepository.findByKey(key)
            .filter(setting -> setting.getUser() == null) // System-wide only
            .map(Setting::getValue)
            .orElse(null);
    }
    
    public void updateSetting(String key, String value) {
        Setting setting = settingRepository.findByKey(key)
            .orElse(new Setting());
        setting.setKey(key);
        setting.setValue(value);
        setting.setUser(null); // System-wide
        settingRepository.save(setting);
    }
}
```

## Integration Notes

### Thymeleaf Templates
Settings are displayed using different input types based on the setting key:

```html
<!-- Boolean Settings -->
<div th:case="'maintenance_mode'">
    <select class="form-select" name="values">
        <option value="false" th:selected="${setting.value == 'false'}">غیرفعال</option>
        <option value="true" th:selected="${setting.value == 'true'}">فعال</option>
    </select>
</div>

<!-- Number Settings -->
<div th:case="'max_upload_size'">
    <input type="number" class="form-control" name="values" th:value="${setting.value}">
</div>

<!-- Email Settings -->
<div th:case="'admin_email'">
    <input type="email" class="form-control" name="values" th:value="${setting.value}">
</div>
```

## Troubleshooting

### Common Issues

1. **Settings not saving:**
   - Check database connectivity
   - Verify user permissions
   - Check validation errors in logs

2. **Page not loading:**
   - Verify user has admin role
   - Check server status
   - Review application logs

3. **Default settings not appearing:**
   - Run CommonDataInitializer
   - Check database tables
   - Verify setting.user is null for system settings

---

**Last Updated:** October 2025  
**Version:** 1.0.0
