package com.webrayan.store.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.file-upload")
public class FileUploadConfig {

    @NotNull
    @Min(1)
    private Long maxFileSize = 10485760L; // 10MB default

    @NotNull
    @Min(1)
    private Integer maxFilesPerRequest = 5;

    @NotEmpty
    private List<String> allowedImageTypes = List.of(
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/gif",
        "image/webp"
    );

    @NotEmpty
    private List<String> allowedImageExtensions = List.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    @NotNull
    private String uploadDirectory = "uploads/";

    @NotNull
    private String tempDirectory = "temp/";

    private boolean enableVirusScanning = false;

    private boolean enableWatermark = false;

    private String watermarkText = "Iran ECommerce";

    private Integer jpegQuality = 85;

    private boolean enableWebpConversion = true;

    // Image size configurations
    private List<ImageSize> imageSizes = List.of(
        new ImageSize("thumbnail", 150, 150),
        new ImageSize("small", 300, 300),
        new ImageSize("medium", 600, 600),
        new ImageSize("large", 1200, 1200)
    );

    // Getters and Setters
    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Integer getMaxFilesPerRequest() {
        return maxFilesPerRequest;
    }

    public void setMaxFilesPerRequest(Integer maxFilesPerRequest) {
        this.maxFilesPerRequest = maxFilesPerRequest;
    }

    public List<String> getAllowedImageTypes() {
        return allowedImageTypes;
    }

    public void setAllowedImageTypes(List<String> allowedImageTypes) {
        this.allowedImageTypes = allowedImageTypes;
    }

    public List<String> getAllowedImageExtensions() {
        return allowedImageExtensions;
    }

    public void setAllowedImageExtensions(List<String> allowedImageExtensions) {
        this.allowedImageExtensions = allowedImageExtensions;
    }

    public String getUploadDirectory() {
        return uploadDirectory;
    }

    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public String getTempDirectory() {
        return tempDirectory;
    }

    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    public boolean isEnableVirusScanning() {
        return enableVirusScanning;
    }

    public void setEnableVirusScanning(boolean enableVirusScanning) {
        this.enableVirusScanning = enableVirusScanning;
    }

    public boolean isEnableWatermark() {
        return enableWatermark;
    }

    public void setEnableWatermark(boolean enableWatermark) {
        this.enableWatermark = enableWatermark;
    }

    public String getWatermarkText() {
        return watermarkText;
    }

    public void setWatermarkText(String watermarkText) {
        this.watermarkText = watermarkText;
    }

    public Integer getJpegQuality() {
        return jpegQuality;
    }

    public void setJpegQuality(Integer jpegQuality) {
        this.jpegQuality = jpegQuality;
    }

    public boolean isEnableWebpConversion() {
        return enableWebpConversion;
    }

    public void setEnableWebpConversion(boolean enableWebpConversion) {
        this.enableWebpConversion = enableWebpConversion;
    }

    public List<ImageSize> getImageSizes() {
        return imageSizes;
    }

    public void setImageSizes(List<ImageSize> imageSizes) {
        this.imageSizes = imageSizes;
    }

    public static class ImageSize {
        private String name;
        private Integer width;
        private Integer height;

        public ImageSize() {}

        public ImageSize(String name, Integer width, Integer height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
    }
}
