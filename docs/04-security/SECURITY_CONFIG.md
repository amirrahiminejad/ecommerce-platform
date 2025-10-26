# ⚙️ پیکربندی‌های امنیتی

## متغیرهای محیطی (Environment Variables)

### تولید (Production)
```bash
# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-here-make-it-very-long-and-random
JWT_EXPIRATION=36000000

# Database Security
DB_URL=jdbc:postgresql://localhost:5432/iran_bazaar
DB_USERNAME=bazaar_user
DB_PASSWORD=secure_database_password

# CORS Settings
ALLOWED_ORIGINS=https://yourdomain.com,https://admin.yourdomain.com

# Security Headers
SECURITY_FRAME_OPTIONS=DENY
SECURITY_CONTENT_TYPE_OPTIONS=nosniff
SECURITY_HSTS_MAX_AGE=31536000
```

### توسعه (Development)
```bash
# JWT Configuration (development only)
JWT_SECRET=dev-secret-key-for-development-only
JWT_EXPIRATION=86400000

# Database
DB_URL=jdbc:postgresql://localhost:5432/iran_bazaar_dev
DB_USERNAME=dev_user
DB_PASSWORD=dev_password

# CORS (development)
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

## application.properties

### Production
```properties
# Security
spring.security.user.name=admin
spring.security.user.password=change-this-in-production
spring.security.user.roles=ADMIN

# JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:36000000}

# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Security Headers
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict

# CORS
cors.allowed-origins=${ALLOWED_ORIGINS}
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
cors.allowed-headers=*
cors.allow-credentials=true
cors.max-age=3600

# Logging
logging.level.com.webrayan.store.core.security=INFO
logging.level.org.springframework.security=WARN
logging.level.org.hibernate.SQL=WARN
```

### Development
```properties
# Security (development)
spring.security.user.name=admin
spring.security.user.password=123
spring.security.user.roles=ADMIN

# JWT (development)
jwt.secret=${JWT_SECRET:dev-secret-key}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Database (development)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/iran_bazaar_dev}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# JPA/Hibernate (development)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Security Headers (relaxed for development)
server.servlet.session.cookie.secure=false

# CORS (development)
cors.allowed-origins=http://localhost:3000,http://127.0.0.1:3000
cors.allow-credentials=true

# Logging (development)
logging.level.com.webrayan.store=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Docker Configuration

### docker-compose.yml
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - JWT_SECRET=${JWT_SECRET}
      - JWT_EXPIRATION=${JWT_EXPIRATION}
      - DB_URL=jdbc:postgresql://db:5432/iran_bazaar
      - DB_USERNAME=bazaar_user
      - DB_PASSWORD=${DB_PASSWORD}
      - ALLOWED_ORIGINS=${ALLOWED_ORIGINS}
    depends_on:
      - db
    networks:
      - bazaar-network

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=iran_bazaar
      - POSTGRES_USER=bazaar_user
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - bazaar-network

volumes:
  postgres_data:

networks:
  bazaar-network:
    driver: bridge
```

### .env (برای Docker)
```bash
# Never commit this file to git!
JWT_SECRET=your-super-secret-256-bit-key-here-make-it-random-and-long
JWT_EXPIRATION=36000000
DB_PASSWORD=your-secure-database-password
ALLOWED_ORIGINS=https://yourdomain.com,https://admin.yourdomain.com
```

## Nginx Configuration

### nginx.conf
```nginx
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;

    # Security Headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin";

    # API Proxy
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # CORS Headers
        add_header Access-Control-Allow-Origin "https://yourdomain.com" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Authorization, Content-Type" always;
        add_header Access-Control-Allow-Credentials true always;
        
        # Handle preflight requests
        if ($request_method = 'OPTIONS') {
            return 204;
        }
    }

    # Static files
    location / {
        root /var/www/html;
        try_files $uri $uri/ /index.html;
    }
}
```

## Database Security

### PostgreSQL Configuration
```sql
-- Create dedicated user
CREATE USER bazaar_user WITH PASSWORD 'secure_password';

-- Create database
CREATE DATABASE iran_bazaar OWNER bazaar_user;

-- Grant permissions
GRANT CONNECT ON DATABASE iran_bazaar TO bazaar_user;
GRANT USAGE ON SCHEMA public TO bazaar_user;
GRANT CREATE ON SCHEMA public TO bazaar_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO bazaar_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO bazaar_user;

-- Security settings in postgresql.conf
ssl = on
ssl_cert_file = '/path/to/server.crt'
ssl_key_file = '/path/to/server.key'
ssl_ciphers = 'HIGH:MEDIUM:+3DES:!aNULL'
ssl_prefer_server_ciphers = on
```

## Monitoring & Logging

### application.properties (Monitoring)
```properties
# Actuator
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.endpoints.web.base-path=/actuator

# Security for actuator
management.security.enabled=true
management.endpoints.web.cors.allowed-origins=${ALLOWED_ORIGINS}

# Metrics
management.metrics.export.prometheus.enabled=true
```

### Logback Configuration (logback-spring.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="production">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/var/log/iran-bazaar/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/var/log/iran-bazaar/application.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <logger name="com.webrayan.store.core.security" level="INFO"/>
        <logger name="org.springframework.security" level="WARN"/>

        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>

    <springProfile name="development">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <logger name="com.webrayan.store" level="DEBUG"/>
        <logger name="org.springframework.security" level="DEBUG"/>

        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
</configuration>
```

## Security Checklist

### Pre-Production
- [ ] JWT Secret key تغییر کرده و قوی است
- [ ] Password های پیش‌فرض تغییر کرده‌اند
- [ ] HTTPS فعال است
- [ ] Security Headers تنظیم شده‌اند
- [ ] CORS فقط برای دامنه‌های مجاز فعال است
- [ ] Database credentials امن هستند
- [ ] Logging مناسب تنظیم شده
- [ ] Actuator endpoints محافظت شده‌اند

### Runtime Security
- [ ] Regular security updates
- [ ] Monitor failed login attempts
- [ ] Review user permissions regularly
- [ ] Backup database securely
- [ ] Monitor system logs
- [ ] Update JWT secrets periodically

## Performance Tuning

### JVM Options
```bash
-Xms512m
-Xmx2g
-XX:+UseG1GC
-XX:+UseStringDeduplication
-XX:+OptimizeStringConcat
```

### Database Connection Pool
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=600000
spring.datasource.hikari.connection-timeout=30000
```
