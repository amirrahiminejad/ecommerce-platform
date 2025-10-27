package com.webrayan.store.core.utils;

import com.webrayan.store.modules.catalog.entity.Product;
import com.webrayan.store.modules.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * کمک‌کننده برای کار با تصاویر محصولات در template ها
 * این کلاس مشکل LazyInitializationException را حل می‌کند
 */
@Component("productImageUtils")
@RequiredArgsConstructor
public class ProductImageUtils {

    private final ProductService productService;

    /**
     * بررسی اینکه آیا محصول تصویری دارد یا نه
     */
    public boolean hasImages(Product product) {
        if (product == null || product.getId() == null) {
            return false;
        }
        return productService.hasImages(product.getId());
    }

    /**
     * دریافت URL اولین تصویر محصول
     */
    public String getFirstImageUrl(Product product) {
        if (product == null || product.getId() == null) {
            return "/images/no-image.png";
        }
        return productService.getFirstImageUrl(product.getId());
    }

    /**
     * دریافت URL تصویر محصول یا تصویر پیش‌فرض
     */
    public String getImageUrlOrDefault(Product product, String defaultUrl) {
        if (product == null || product.getId() == null) {
            return defaultUrl;
        }
        String imageUrl = productService.getFirstImageUrl(product.getId());
        return imageUrl.equals("/images/no-image.png") ? defaultUrl : imageUrl;
    }

    /**
     * دریافت تعداد تصاویر محصول
     */
    public int getImageCount(Product product) {
        if (product == null || product.getId() == null) {
            return 0;
        }
        return productService.getProductByIdWithImages(product.getId())
                .map(p -> p.getImages() != null ? p.getImages().size() : 0)
                .orElse(0);
    }

    /**
     * دریافت نام دسته‌بندی محصول بدون lazy loading exception
     */
    public String getCategoryName(Product product) {
        if (product == null || product.getId() == null) {
            return "نامشخص";
        }
        return productService.getProductByIdWithImages(product.getId())
                .map(p -> p.getCategory() != null ? p.getCategory().getName() : "نامشخص")
                .orElse("نامشخص");
    }

    /**
     * دریافت نام فروشنده محصول بدون lazy loading exception
     */
    public String getSellerName(Product product) {
        if (product == null || product.getId() == null) {
            return "نامشخص";
        }
        return productService.getProductByIdWithImages(product.getId())
                .map(p -> p.getSeller() != null ? p.getSeller().getFirstName() + " " + p.getSeller().getLastName() : "نامشخص")
                .orElse("نامشخص");
    }
}
