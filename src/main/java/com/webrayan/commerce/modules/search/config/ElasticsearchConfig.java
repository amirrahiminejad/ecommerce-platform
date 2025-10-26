package com.webrayan.commerce.modules.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * تنظیمات Elasticsearch
 */
@Configuration
@ConfigurationProperties(prefix = "app.elasticsearch")
@Data
public class ElasticsearchConfig {

    /**
     * آیا Elasticsearch فعال است
     */
    private Boolean enabled = false;

    /**
     * آدرس سرور Elasticsearch
     */
    private String host = "localhost";

    /**
     * پورت سرور Elasticsearch
     */
    private Integer port = 9200;

    /**
     * نام کاربری (اختیاری)
     */
    private String username;

    /**
     * رمز عبور (اختیاری)
     */
    private String password;

    /**
     * استفاده از HTTPS
     */
    private Boolean useHttps = false;

    /**
     * تنظیمات Index ها
     */
    private IndexSettings index = new IndexSettings();

    @Data
    public static class IndexSettings {
        /**
         * پیشوند نام index ها
         */
        private String prefix = "iran_bazaar";

        /**
         * نام index آگهی‌ها
         */
        private String adsIndex = "ads";

        /**
         * نام index محصولات
         */
        private String productsIndex = "products";

        /**
         * تعداد شارد ها
         */
        private Integer numberOfShards = 1;

        /**
         * تعداد رپلیکا ها
         */
        private Integer numberOfReplicas = 1;

        /**
         * تنظیمات آنالایزر
         */
        private AnalyzerSettings analyzer = new AnalyzerSettings();
    }

    @Data
    public static class AnalyzerSettings {
        /**
         * آنالایزر پیش‌فرض
         */
        private String defaultAnalyzer = "standard";

        /**
         * آنالایزر جستجو
         */
        private String searchAnalyzer = "standard";

        /**
         * زبان متن
         */
        private String language = "english";
    }

    /**
     * دریافت نام کامل index
     */
    public String getFullIndexName(String indexName) {
        return index.getPrefix() + "_" + indexName;
    }

    /**
     * دریافت آدرس کامل سرور
     */
    public String getFullServerUrl() {
        String protocol = useHttps ? "https" : "http";
        return String.format("%s://%s:%d", protocol, host, port);
    }
}
