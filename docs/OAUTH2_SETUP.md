# راهنمای تنظیم ورود با Gmail (OAuth2)

## 📋 مقدمه
این راهنما شما را قدم به قدم برای راه‌اندازی ورود با Gmail در پروژه پرشیا بازار راهنمایی می‌کند.

## 🔧 تنظیمات Google Cloud Console

### 1. ایجاد پروژه در Google Cloud Console

1. به [Google Cloud Console](https://console.cloud.google.com/) بروید
2. روی **"Select a project"** کلیک کنید
3. **"New Project"** را انتخاب کنید
4. نام پروژه را وارد کنید (مثل "Iran ECommerce")
5. روی **"Create"** کلیک کنید

### 2. فعال‌سازی Google+ API

1. در منوی سمت چپ، **"APIs & Services"** > **"Library"** را انتخاب کنید
2. **"Google+ API"** را جستجو کنید
3. روی **"Google+ API"** کلیک کرده و **"Enable"** کنید

### 3. ایجاد OAuth2 Credentials

1. به **"APIs & Services"** > **"Credentials"** بروید
2. **"+ CREATE CREDENTIALS"** > **"OAuth client ID"** را انتخاب کنید
3. اگر اولین بار است، **"Configure Consent Screen"** را کلیک کنید:

#### تنظیم OAuth Consent Screen:
```
App name: Iran ECommerce
User support email: your-email@gmail.com
Developer contact information: your-email@gmail.com
Scopes: Add email, profile, openid
Test users: اضافه کردن ایمیل‌های تست (اختیاری)
```

#### ایجاد OAuth Client ID:
```
Application type: Web application
Name: Iran ECommerce Web Client
Authorized JavaScript origins:
- http://localhost:8005
- https://your-domain.com (برای production)

Authorized redirect URIs:
- http://localhost:8005/login/oauth2/code/google
- https://your-domain.com/login/oauth2/code/google (برای production)
```

4. روی **"Create"** کلیک کنید
5. **Client ID** و **Client Secret** را کپی کنید

## 🔐 تنظیمات Environment Variables

### روش 1: فایل .env (توصیه شده برای Development)

فایل `.env` در root پروژه ایجاد کنید:
```env
GOOGLE_CLIENT_ID=your_actual_google_client_id_here
GOOGLE_CLIENT_SECRET=your_actual_google_client_secret_here
```

### روش 2: متغیرهای سیستم (برای Production)

#### Windows:
```powershell
$env:GOOGLE_CLIENT_ID="your_actual_google_client_id_here"
$env:GOOGLE_CLIENT_SECRET="your_actual_google_client_secret_here"
```

#### Linux/Mac:
```bash
export GOOGLE_CLIENT_ID="your_actual_google_client_id_here"
export GOOGLE_CLIENT_SECRET="your_actual_google_client_secret_here"
```

### روش 3: مستقیم در application.properties (فقط برای تست)

```properties
spring.security.oauth2.client.registration.google.client-id=your_actual_google_client_id_here
spring.security.oauth2.client.registration.google.client-secret=your_actual_google_client_secret_here
```

⚠️ **هشدار امنیتی**: هرگز Client Secret را در کد commit نکنید!

## 🗄️ تنظیمات دیتابیس

دیتابیس شما باید فیلدهای جدید OAuth2 را داشته باشد. migration زیر را اجرا کنید:

```sql
ALTER TABLE acl_users 
ADD COLUMN oauth_provider VARCHAR(50),
ADD COLUMN oauth_provider_id VARCHAR(255),
ADD COLUMN profile_picture VARCHAR(500),
ADD COLUMN verified BOOLEAN DEFAULT FALSE;

-- ایندکس برای بهبود performance
CREATE INDEX idx_oauth_provider ON acl_users(oauth_provider, oauth_provider_id);
```

## 🚀 راه‌اندازی در محیط‌های مختلف

### Development (محلی)
```bash
# 1. تنظیم متغیرها
export GOOGLE_CLIENT_ID="your_client_id"
export GOOGLE_CLIENT_SECRET="your_client_secret"

# 2. اجرای پروژه
./mvnw spring-boot:run
```

### Production (سرور)

#### Docker:
```dockerfile
FROM openjdk:17-jre-slim
COPY target/iran-bazaar.jar app.jar
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### Docker Compose:
```yaml
services:
  iran-bazaar:
    build: .
    ports:
      - "8080:8080"
    environment:
      - GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
      - GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/commerce
    depends_on:
      - db
```

#### Systemd Service:
```ini
[Unit]
Description=Iran ECommerce Application
After=network.target

[Service]
Type=simple
User=bazaar
Environment=GOOGLE_CLIENT_ID=your_client_id
Environment=GOOGLE_CLIENT_SECRET=your_client_secret
ExecStart=/usr/bin/java -jar /opt/iran-bazaar/iran-bazaar.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

## 🔍 تست و عیب‌یابی

### 1. بررسی URL ها

مطمئن شوید که redirect URها دقیقاً منطبق باشند:
- Development: `http://localhost:8005/login/oauth2/code/google`
- Production: `https://yourdomain.com/login/oauth2/code/google`

### 2. بررسی لاگ‌ها

```bash
# مشاهده لاگ‌های Spring Security
logging.level.org.springframework.security=DEBUG

# مشاهده لاگ‌های OAuth2
logging.level.org.springframework.security.oauth2=DEBUG
```

### 3. خطاهای متداول

#### خطا: "Error 400: redirect_uri_mismatch"
**راه حل**: redirect URI در Google Console را بررسی کنید

#### خطا: "Error 401: invalid_client"
**راه حل**: Client ID یا Secret را بررسی کنید

#### خطا: "This app isn't verified"
**راه حل**: در محیط development، روی "Advanced" > "Go to Iran ECommerce (unsafe)" کلیک کنید

## 🛡️ نکات امنیتی

### 1. مخفی نگه‌داشتن Client Secret
```bash
# در .gitignore اضافه کنید
.env
*.env
application-secrets.properties
```

### 2. HTTPS در Production
```properties
# فقط HTTPS
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
```

### 3. Scope محدود
```properties
# فقط اطلاعات ضروری
spring.security.oauth2.client.registration.google.scope=openid,profile,email
```

## 📊 مانیتورینگ

### Spring Boot Actuator
```properties
# فعال‌سازی health endpoint
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

### کسب آمار ورود
```java
// در کد خود log کنید
@EventListener
public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
    log.info("OAuth2 login successful: {}", event.getAuthentication().getName());
}
```

## 🎯 تست کامل

### 1. تست دستی
1. به `http://localhost:8005/auth/login` بروید
2. روی "Continue with Google" کلیک کنید
3. با اکانت Gmail خود وارد شوید
4. بررسی کنید که به پروفایل منتقل شدید

### 2. تست خودکار
```java
@SpringBootTest
@AutoConfigureTestDatabase
class OAuth2IntegrationTest {
    
    @Test
    @WithMockUser
    void testOAuth2LoginFlow() {
        // تست OAuth2 flow
    }
}
```

## 📞 پشتیبانی

اگر مشکلی پیش آمد:
1. ابتدا documentation Google OAuth2 را مطالعه کنید
2. لاگ‌های application را بررسی کنید
3. Redirect URها را دوباره چک کنید
4. Client ID/Secret را مجدداً کپی کنید

---

## ✅ Checklist قبل از Production

- [ ] Client ID/Secret در environment variables قرار دارد
- [ ] Redirect URها برای domain اصلی تنظیم شده
- [ ] HTTPS فعال است
- [ ] OAuth Consent Screen کامل شده
- [ ] دیتابیس migration اجرا شده
- [ ] لاگ‌ها کنترل شده
- [ ] تست کامل انجام شده

**موفق باشید!** 🎉
