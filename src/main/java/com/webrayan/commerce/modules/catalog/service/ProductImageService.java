package com.webrayan.commerce.modules.catalog.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Product image management service with gallery support
 */
public interface ProductImageService {

    /**
     * Upload images for a product
     */
    ProductImageUploadResult uploadProductImages(Long productId, List<MultipartFile> files, ProductImageOptions options) throws IOException;

    /**
     * Set primary image for a product
     */
    void setPrimaryImage(Long productId, Long imageId) throws IOException;

    /**
     * Reorder product images
     */
    void reorderImages(Long productId, List<Long> imageIds) throws IOException;

    /**
     * Get all images for a product
     */
    List<ProductImageInfo> getProductImages(Long productId);

    /**
     * Get primary image for a product
     */
    ProductImageInfo getPrimaryImage(Long productId);

    /**
     * Delete product image
     */
    void deleteProductImage(Long productId, Long imageId) throws IOException;

    /**
     * Delete all images for a product
     */
    void deleteAllProductImages(Long productId) throws IOException;

    /**
     * Get image by size
     */
    byte[] getProductImageBySize(Long imageId, String sizeName) throws IOException;

    /**
     * Generate product image gallery
     */
    ProductImageGallery generateImageGallery(Long productId);

    /**
     * Validate product images according to category requirements
     */
    ProductImageValidationResult validateProductImages(Long categoryId, List<MultipartFile> files);

    /**
     * Product image options
     */
    class ProductImageOptions {
        private boolean setPrimaryAutomatically = true;
        private boolean enableWatermark = true;
        private List<String> requiredSizes = List.of("thumbnail", "small", "medium", "large");
        private int maxImagesPerProduct = 10;
        private boolean enableSeoOptimization = true;

        // Getters and setters
        public boolean isSetPrimaryAutomatically() { return setPrimaryAutomatically; }
        public void setSetPrimaryAutomatically(boolean setPrimaryAutomatically) { this.setPrimaryAutomatically = setPrimaryAutomatically; }

        public boolean isEnableWatermark() { return enableWatermark; }
        public void setEnableWatermark(boolean enableWatermark) { this.enableWatermark = enableWatermark; }

        public List<String> getRequiredSizes() { return requiredSizes; }
        public void setRequiredSizes(List<String> requiredSizes) { this.requiredSizes = requiredSizes; }

        public int getMaxImagesPerProduct() { return maxImagesPerProduct; }
        public void setMaxImagesPerProduct(int maxImagesPerProduct) { this.maxImagesPerProduct = maxImagesPerProduct; }

        public boolean isEnableSeoOptimization() { return enableSeoOptimization; }
        public void setEnableSeoOptimization(boolean enableSeoOptimization) { this.enableSeoOptimization = enableSeoOptimization; }
    }

    /**
     * Product image upload result
     */
    class ProductImageUploadResult {
        private List<ProductImageInfo> uploadedImages;
        private ProductImageInfo primaryImage;
        private List<String> errors;

        public ProductImageUploadResult(List<ProductImageInfo> uploadedImages, ProductImageInfo primaryImage, List<String> errors) {
            this.uploadedImages = uploadedImages;
            this.primaryImage = primaryImage;
            this.errors = errors;
        }

        // Getters and setters
        public List<ProductImageInfo> getUploadedImages() { return uploadedImages; }
        public void setUploadedImages(List<ProductImageInfo> uploadedImages) { this.uploadedImages = uploadedImages; }

        public ProductImageInfo getPrimaryImage() { return primaryImage; }
        public void setPrimaryImage(ProductImageInfo primaryImage) { this.primaryImage = primaryImage; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }

    /**
     * Product image information
     */
    class ProductImageInfo {
        private Long id;
        private Long productId;
        private String filename;
        private String originalFilename;
        private boolean isPrimary;
        private int sortOrder;
        private String altText;
        private List<ImageVariantInfo> variants;
        private java.time.LocalDateTime uploadedAt;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        public String getOriginalFilename() { return originalFilename; }
        public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

        public boolean isPrimary() { return isPrimary; }
        public void setPrimary(boolean primary) { isPrimary = primary; }

        public int getSortOrder() { return sortOrder; }
        public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

        public String getAltText() { return altText; }
        public void setAltText(String altText) { this.altText = altText; }

        public List<ImageVariantInfo> getVariants() { return variants; }
        public void setVariants(List<ImageVariantInfo> variants) { this.variants = variants; }

        public java.time.LocalDateTime getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(java.time.LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    }

    /**
     * Image variant information
     */
    class ImageVariantInfo {
        private String sizeName;
        private int width;
        private int height;
        private String format;
        private long fileSize;
        private String url;

        public ImageVariantInfo(String sizeName, int width, int height, String format, long fileSize, String url) {
            this.sizeName = sizeName;
            this.width = width;
            this.height = height;
            this.format = format;
            this.fileSize = fileSize;
            this.url = url;
        }

        // Getters and setters
        public String getSizeName() { return sizeName; }
        public void setSizeName(String sizeName) { this.sizeName = sizeName; }

        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }

        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }

        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }

        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    /**
     * Product image gallery
     */
    class ProductImageGallery {
        private ProductImageInfo primaryImage;
        private List<ProductImageInfo> additionalImages;
        private int totalImages;

        public ProductImageGallery(ProductImageInfo primaryImage, List<ProductImageInfo> additionalImages, int totalImages) {
            this.primaryImage = primaryImage;
            this.additionalImages = additionalImages;
            this.totalImages = totalImages;
        }

        // Getters and setters
        public ProductImageInfo getPrimaryImage() { return primaryImage; }
        public void setPrimaryImage(ProductImageInfo primaryImage) { this.primaryImage = primaryImage; }

        public List<ProductImageInfo> getAdditionalImages() { return additionalImages; }
        public void setAdditionalImages(List<ProductImageInfo> additionalImages) { this.additionalImages = additionalImages; }

        public int getTotalImages() { return totalImages; }
        public void setTotalImages(int totalImages) { this.totalImages = totalImages; }
    }

    /**
     * Product image validation result
     */
    class ProductImageValidationResult {
        private boolean valid;
        private List<String> errors;
        private List<String> warnings;

        public ProductImageValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = errors;
            this.warnings = warnings;
        }

        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }
}
