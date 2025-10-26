package com.webrayan.commerce.modules.acl.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "acl_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_name", unique = true, nullable = false, length = 100)
    private PermissionName permissionName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PermissionName {
        // User Management
        USER_CREATE("ایجاد کاربر", "USER", "CREATE"),
        USER_READ("مشاهده کاربر", "USER", "READ"),
        USER_UPDATE("ویرایش کاربر", "USER", "UPDATE"),
        USER_DELETE("حذف کاربر", "USER", "DELETE"),
        USER_MANAGE_ROLES("مدیریت نقش‌ها", "USER", "MANAGE_ROLES"),

        // Product Management
        PRODUCT_CREATE("ایجاد محصول", "PRODUCT", "CREATE"),
        PRODUCT_READ("مشاهده محصول", "PRODUCT", "READ"),
        PRODUCT_UPDATE("ویرایش محصول", "PRODUCT", "UPDATE"),
        PRODUCT_DELETE("حذف محصول", "PRODUCT", "DELETE"),
        PRODUCT_MANAGE_INVENTORY("مدیریت موجودی", "PRODUCT", "MANAGE_INVENTORY"),

        // Order Management
        ORDER_CREATE("ایجاد سفارش", "ORDER", "CREATE"),
        ORDER_READ("مشاهده سفارش", "ORDER", "READ"),
        ORDER_UPDATE("ویرایش سفارش", "ORDER", "UPDATE"),
        ORDER_DELETE("حذف سفارش", "ORDER", "DELETE"),
        ORDER_CANCEL("لغو سفارش", "ORDER", "CANCEL"),
        ORDER_FULFILL("تکمیل سفارش", "ORDER", "FULFILL"),

        // Payment Management
        PAYMENT_PROCESS("پردازش پرداخت", "PAYMENT", "PROCESS"),
        PAYMENT_REFUND("استرداد پرداخت", "PAYMENT", "REFUND"),
        PAYMENT_VIEW("مشاهده پرداخت", "PAYMENT", "VIEW"),

        // Inventory Management
        INVENTORY_READ("مشاهده موجودی", "INVENTORY", "READ"),
        INVENTORY_UPDATE("ویرایش موجودی", "INVENTORY", "UPDATE"),
        INVENTORY_TRANSFER("انتقال موجودی", "INVENTORY", "TRANSFER"),

        // Affiliate Management
        AFFILIATE_CREATE("ایجاد بازاریاب", "AFFILIATE", "CREATE"),
        AFFILIATE_READ("مشاهده بازاریاب", "AFFILIATE", "READ"),
        AFFILIATE_UPDATE("ویرایش بازاریاب", "AFFILIATE", "UPDATE"),
        AFFILIATE_MANAGE_COMMISSION("مدیریت کمیسیون", "AFFILIATE", "MANAGE_COMMISSION"),

        // Reporting
        REPORT_SALES("گزارش فروش", "REPORT", "SALES"),
        REPORT_INVENTORY("گزارش موجودی", "REPORT", "INVENTORY"),
        REPORT_FINANCIAL("گزارش مالی", "REPORT", "FINANCIAL"),
        REPORT_USER("گزارش کاربران", "REPORT", "USER"),

        // System Administration
        SYSTEM_CONFIG("تنظیمات سیستم", "SYSTEM", "CONFIG"),
        SYSTEM_BACKUP("پشتیبان‌گیری", "SYSTEM", "BACKUP"),
        SYSTEM_MAINTENANCE("نگهداری سیستم", "SYSTEM", "MAINTENANCE");

        private final String persianName;
        private final String resource;
        private final String action;

        PermissionName(String persianName, String resource, String action) {
            this.persianName = persianName;
            this.resource = resource;
            this.action = action;
        }

        public String getPersianName() {
            return persianName;
        }

        public String getResource() {
            return resource;
        }

        public String getAction() {
            return action;
        }
    }
}
