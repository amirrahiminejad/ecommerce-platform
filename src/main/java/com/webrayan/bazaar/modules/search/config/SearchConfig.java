package com.webrayan.bazaar.modules.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * تنظیمات عمومی جستجو
 */
@Configuration
@ConfigurationProperties(prefix = "app.search")
@Data
public class SearchConfig {

    /**
     * حداکثر تعداد نتایج در صفحه
     */
    private Integer maxPageSize = 100;

    /**
     * حداکثر تعداد کل نتایج
     */
    private Long maxTotalResults = 10000L;

    /**
     * حداکثر طول کلمه کلیدی
     */
    private Integer maxKeywordLength = 200;

    /**
     * حداقل طول کلمه کلیدی برای جستجو
     */
    private Integer minKeywordLength = 2;

    /**
     * تعداد پیشنهادات autocomplete
     */
    private Integer maxSuggestions = 10;

    /**
     * زمان انقضای cache (ثانیه)
     */
    private Integer cacheExpirationSeconds = 3600; // 1 ساعت

    /**
     * آیا cache فعال است
     */
    private Boolean cacheEnabled = true;

    /**
     * آیا لاگ جستجو فعال است
     */
    private Boolean searchLoggingEnabled = true;

    /**
     * تنظیمات Performance
     */
    private PerformanceSettings performance = new PerformanceSettings();

    /**
     * تنظیمات relevance scoring
     */
    private ScoringSettings scoring = new ScoringSettings();

    @Data
    public static class PerformanceSettings {
        /**
         * Timeout جستجو (میلی‌ثانیه)
         */
        private Long searchTimeoutMs = 30000L; // 30 ثانیه

        /**
         * تعداد thread های موازی
         */
        private Integer parallelThreads = 4;

        /**
         * استفاده از connection pooling
         */
        private Boolean useConnectionPooling = true;

        /**
         * حداکثر تعداد connection
         */
        private Integer maxConnections = 20;
    }

    @Data
    public static class ScoringSettings {
        /**
         * وزن جستجو در عنوان
         */
        private Double titleWeight = 2.0;

        /**
         * وزن جستجو در توضیحات
         */
        private Double descriptionWeight = 1.0;

        /**
         * وزن تاریخ (جدیدتر = بالاتر)
         */
        private Double dateWeight = 0.5;

        /**
         * وزن محبوبیت (بازدید)
         */
        private Double popularityWeight = 0.3;

        /**
         * وزن قیمت (کمتر = بهتر)
         */
        private Double priceWeight = 0.2;
    }

    /**
     * بررسی معتبر بودن کلمه کلیدی
     */
    public boolean isValidKeyword(String keyword) {
        if (keyword == null) return false;
        
        int length = keyword.trim().length();
        return length >= minKeywordLength && length <= maxKeywordLength;
    }

    /**
     * بررسی معتبر بودن اندازه صفحه
     */
    public boolean isValidPageSize(Integer size) {
        return size != null && size > 0 && size <= maxPageSize;
    }

    /**
     * تنظیم اندازه صفحه در محدوده مجاز
     */
    public Integer normalizePageSize(Integer size) {
        if (size == null || size <= 0) return 20; // پیش‌فرض
        return Math.min(size, maxPageSize);
    }
}
