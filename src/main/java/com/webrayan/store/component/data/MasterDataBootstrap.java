package com.webrayan.store.component.data;

import com.webrayan.store.component.data.initializers.CatalogDataInitializer;
import com.webrayan.store.component.data.initializers.CommonDataInitializer;
import com.webrayan.store.component.data.initializers.SecurityDataInitializer;
import com.webrayan.store.component.data.initializers.SaleDataInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev") // فقط در profile dev اجرا شود
@Order(1) // اولین Bootstrap که اجرا می‌شود
public class MasterDataBootstrap implements CommandLineRunner {

    private final CommonDataInitializer commonDataInitializer;
    private final SecurityDataInitializer securityDataInitializer;
    private final CatalogDataInitializer catalogDataInitializer;
    private final SaleDataInitializer saleDataInitializer;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // 1. داده‌های مشترک (کشورها، تنظیمات، برچسب‌ها)
            commonDataInitializer.initialize();
            
            // 2. داده‌های امنیتی (کاربران، نقش‌ها، مجوزها)
            securityDataInitializer.initialize();
            
            // 3. داده‌های کاتالوگ (دسته‌بندی محصولات)
            catalogDataInitializer.initialize();
            
            // 4. داده‌های فروش (سفارشات نمونه)
            saleDataInitializer.initialize();

        } catch (Exception e) {
            log.error("❌ Error on create data: {}", e.getMessage(), e);
            throw e;
        }
    }
}
