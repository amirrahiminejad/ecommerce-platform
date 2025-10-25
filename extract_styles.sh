#!/bin/bash

# Script to extract inline styles from HTML templates
# این اسکریپت برای استخراج استایل‌های inline از قالب‌های HTML

echo "شروع استخراج استایل‌های inline..."

# لیست فایل‌هایی که باید پردازش شوند
files=(
    "src/main/resources/templates/auth/register.html"
    "src/main/resources/templates/auth/forgot-password.html"
    "src/main/resources/templates/auth/reset-password.html"
    "src/main/resources/templates/profile/index.html"
    "src/main/resources/templates/profile/edit.html"
    "src/main/resources/templates/profile/change-password.html"
    "src/main/resources/templates/profile/messages.html"
    "src/main/resources/templates/profile/ads.html"
    "src/main/resources/templates/ads/create.html"
    "src/main/resources/templates/ads/view.html"
    "src/main/resources/templates/admin/login.html"
    "src/main/resources/templates/admin/pages/dashboard.html"
    "src/main/resources/templates/admin/pages/users.html"
    "src/main/resources/templates/admin/pages/products.html"
    "src/main/resources/templates/admin/pages/ads.html"
    "src/main/resources/templates/components/confirm-modal.html"
    "src/main/resources/templates/test/components.html"
)

# برای هر فایل
for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "پردازش فایل: $file"
        
        # جستجوی بلوک‌های <style>
        if grep -q "<style>" "$file"; then
            echo "  - یافتن استایل inline در $file"
            
            # استخراج محتویات بین <style> و </style>
            sed -n '/<style>/,/<\/style>/p' "$file" > "temp_styles_$(basename "$file" .html).css"
            
            # حذف تگ‌های <style> و </style>
            sed -i 's/<style>//g; s/<\/style>//g' "temp_styles_$(basename "$file" .html).css"
            
            echo "  - استایل‌ها در temp_styles_$(basename "$file" .html).css ذخیره شد"
        else
            echo "  - هیچ استایل inline در $file یافت نشد"
        fi
    else
        echo "فایل $file یافت نشد"
    fi
done

echo "استخراج کامل شد!"
echo ""
echo "فایل‌های CSS موقت ایجاد شده:"
ls -la temp_styles_*.css 2>/dev/null || echo "هیچ فایل موقتی ایجاد نشد"
