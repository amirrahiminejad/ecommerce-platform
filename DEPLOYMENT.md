# فروشگاه اینترنتی - Online Store Deployment Guide

این راهنما برای deploy کردن پروژه فروشگاه اینترنتی روی سرور Linux با Tomcat طراحی شده است.

## 📋 پیش‌نیازها

### سرور Requirements:
- Ubuntu/Debian Linux Server
- Java 17+
- PostgreSQL 12+
- Tomcat 10+
- Nginx (اختیاری برای reverse proxy)

### Local Development:
- Maven 3.6+
- Java 17+
- Git

## 🚀 راه‌اندازی سرور (یکبار)

### 1. کپی فایل‌ها به سرور:
```bash
scp -r iran-commerce/ user@your-server:/opt/
```

### 2. اجرای اسکریپت setup:
```bash
cd /opt/iran-commerce
sudo ./server-setup.sh
```

این اسکریپت موارد زیر را انجام می‌دهد:
- نصب Java 17, PostgreSQL, Tomcat, Nginx
- ایجاد کاربر tomcat
- تنظیم دایرکتوری‌های پروژه
- پیکربندی systemd service
- تنظیم firewall و nginx

## 📦 Deployment

### روش اول: استفاده از اسکریپت خودکار

```bash
# Deploy اولیه
sudo ./deploy.sh deploy production

# چک کردن وضعیت
sudo ./deploy.sh status

# مشاهده لاگ‌ها
sudo ./deploy.sh logs

# Rollback در صورت مشکل
sudo ./deploy.sh rollback
```

### روش دوم: Manual Deployment

```bash
# 1. Build کردن WAR
./mvnw clean package -DskipTests -Dspring.profiles.active=prod

# 2. توقف Tomcat
sudo systemctl stop tomcat

# 3. کپی WAR جدید
sudo cp target/iran-commerce-*.war /opt/tomcat/webapps/iran-commerce.war

# 4. راه‌اندازی Tomcat
sudo systemctl start tomcat

# 5. چک کردن وضعیت
sudo systemctl status tomcat
```

## 🔧 تنظیمات محیط Production

### متغیرهای محیطی مهم:
```bash
# در /opt/tomcat/bin/setenv.sh
export DB_PASSWORD="your_secure_password"
export JWT_SECRET="your_long_secure_jwt_secret"
export MAIL_USERNAME="your_email@gmail.com"
export MAIL_PASSWORD="your_app_password"
export GOOGLE_CLIENT_ID="your_google_client_id"
export GOOGLE_CLIENT_SECRET="your_google_client_secret"
```

### تنظیمات دیتابیس:
```sql
-- ایجاد دیتابیس
CREATE DATABASE bazaar;

-- تنظیم رمز عبور قوی برای postgres
ALTER USER postgres PASSWORD 'new_secure_password';

-- یا ایجاد کاربر جدید
CREATE USER bazaar_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE bazaar TO bazaar_user;
```

## 🌐 دسترسی به برنامه

پس از deployment موفق:

- **برنامه اصلی**: `http://your-server:8080/online-store`
- **از طریق Nginx**: `http://your-server/`
- **Swagger UI**: `http://your-server:8080/online-store/swagger-ui.html`
- **پنل ادمین**: `http://your-server:8080/online-store/admin`

## 📊 مانیتورینگ و لاگ‌ها

### لاگ‌های مهم:
```bash
# لاگ برنامه
tail -f /opt/iran-commerce/logs/application.log

# لاگ Tomcat
tail -f /opt/tomcat/logs/catalina.out

# لاگ Nginx
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### کامندهای مفید:
```bash
# وضعیت سرویس‌ها
systemctl status tomcat
systemctl status nginx
systemctl status postgresql

# ری‌استارت سرویس‌ها
systemctl restart tomcat
systemctl restart nginx

# فضای دیسک
df -h
du -sh /opt/iran-commerce/*

# پردازه‌های Java
jps -v
```

## 🔒 امنیت

### تنظیمات امنیتی مهم:

1. **تغییر رمزهای پیش‌فرض**:
   - PostgreSQL password
   - JWT secret key
   - Admin panel credentials

2. **فایروال**:
   ```bash
   ufw allow 22    # SSH
   ufw allow 80    # HTTP
   ufw allow 443   # HTTPS
   ufw enable
   ```

3. **SSL Certificate** (Production):
   ```bash
   # با Let's Encrypt
   apt install certbot python3-certbot-nginx
   certbot --nginx -d your-domain.com
   ```

## 🔄 Backup و Recovery

### Backup خودکار:
```bash
# اسکریپت backup دیتابیس
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -U postgres commerce > /opt/iran-commerce/backups/db_backup_$DATE.sql
```

### Restore:
```bash
# بازگردانی دیتابیس
psql -U postgres -d commerce < /opt/iran-commerce/backups/db_backup_YYYYMMDD_HHMMSS.sql
```

## 🐛 Troubleshooting

### مشکلات رایج:

1. **Application شروع نمی‌شود**:
   ```bash
   # چک لاگ‌ها
   sudo ./deploy.sh logs
   tail -f /opt/tomcat/logs/catalina.out
   ```

2. **خطای دیتابیس**:
   ```bash
   # تست اتصال
   psql -U postgres -h localhost -d commerce
   ```

3. **خطای فایل upload**:
   ```bash
   # چک مجوزها
   ls -la /opt/iran-commerce/uploads/
   chown -R tomcat:tomcat /opt/iran-commerce/uploads/
   ```

4. **خطای حافظه**:
   ```bash
   # افزایش حافظه Tomcat در /etc/systemd/system/tomcat.service
   Environment="CATALINA_OPTS=-Xms1G -Xmx4G"
   systemctl daemon-reload
   systemctl restart tomcat
   ```

## 📞 پشتیبانی

برای مشکلات و سوالات:
- چک کردن لاگ‌ها: `sudo ./deploy.sh logs`
- وضعیت سیستم: `sudo ./deploy.sh status`
- Rollback: `sudo ./deploy.sh rollback`

---

**توجه**: این راهنما برای محیط production طراحی شده. برای development از profile `dev` استفاده کنید.
