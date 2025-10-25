package com.webrayan.bazaar.core.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Advanced image processing service with optimization, format conversion, and watermarking
 */
public interface ImageProcessingService {

    /**
     * Process image with resizing, optimization, and format conversion
     */
    ImageProcessingResult processImage(MultipartFile file, ImageProcessingOptions options) throws IOException;

    /**
     * Process multiple images
     */
    List<ImageProcessingResult> processImages(List<MultipartFile> files, ImageProcessingOptions options) throws IOException;

    /**
     * Resize image to specific dimensions
     */
    byte[] resizeImage(byte[] imageData, int width, int height, ResizeMode mode) throws IOException;

    /**
     * Convert image format
     */
    byte[] convertFormat(byte[] imageData, String sourceFormat, String targetFormat) throws IOException;

    /**
     * Add watermark to image
     */
    byte[] addWatermark(byte[] imageData, WatermarkOptions watermarkOptions) throws IOException;

    /**
     * Optimize image quality and size
     */
    byte[] optimizeImage(byte[] imageData, String format, float quality) throws IOException;

    /**
     * Generate image thumbnail
     */
    byte[] generateThumbnail(byte[] imageData, int size) throws IOException;

    /**
     * Extract image metadata
     */
    ImageMetadata extractMetadata(byte[] imageData) throws IOException;

    /**
     * Image processing options
     */
    class ImageProcessingOptions {
        private List<ImageSize> sizes;
        private boolean enableWebpConversion;
        private boolean enableWatermark;
        private WatermarkOptions watermarkOptions;
        private float jpegQuality = 0.85f;
        private boolean enableProgressive;
        private ResizeMode resizeMode = ResizeMode.PROPORTIONAL;

        // Getters and setters
        public List<ImageSize> getSizes() { return sizes; }
        public void setSizes(List<ImageSize> sizes) { this.sizes = sizes; }

        public boolean isEnableWebpConversion() { return enableWebpConversion; }
        public void setEnableWebpConversion(boolean enableWebpConversion) { this.enableWebpConversion = enableWebpConversion; }

        public boolean isEnableWatermark() { return enableWatermark; }
        public void setEnableWatermark(boolean enableWatermark) { this.enableWatermark = enableWatermark; }

        public WatermarkOptions getWatermarkOptions() { return watermarkOptions; }
        public void setWatermarkOptions(WatermarkOptions watermarkOptions) { this.watermarkOptions = watermarkOptions; }

        public float getJpegQuality() { return jpegQuality; }
        public void setJpegQuality(float jpegQuality) { this.jpegQuality = jpegQuality; }

        public boolean isEnableProgressive() { return enableProgressive; }
        public void setEnableProgressive(boolean enableProgressive) { this.enableProgressive = enableProgressive; }

        public ResizeMode getResizeMode() { return resizeMode; }
        public void setResizeMode(ResizeMode resizeMode) { this.resizeMode = resizeMode; }
    }

    /**
     * Image size specification
     */
    class ImageSize {
        private String name;
        private int width;
        private int height;
        private boolean maintainAspectRatio = true;

        public ImageSize() {}

        public ImageSize(String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public ImageSize(String name, int width, int height, boolean maintainAspectRatio) {
            this.name = name;
            this.width = width;
            this.height = height;
            this.maintainAspectRatio = maintainAspectRatio;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }

        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }

        public boolean isMaintainAspectRatio() { return maintainAspectRatio; }
        public void setMaintainAspectRatio(boolean maintainAspectRatio) { this.maintainAspectRatio = maintainAspectRatio; }
    }

    /**
     * Watermark options
     */
    class WatermarkOptions {
        private String text;
        private WatermarkPosition position = WatermarkPosition.BOTTOM_RIGHT;
        private float opacity = 0.5f;
        private int fontSize = 24;
        private String fontColor = "#FFFFFF";
        private String backgroundColor = "#000000";
        private int margin = 10;

        // Getters and setters
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public WatermarkPosition getPosition() { return position; }
        public void setPosition(WatermarkPosition position) { this.position = position; }

        public float getOpacity() { return opacity; }
        public void setOpacity(float opacity) { this.opacity = opacity; }

        public int getFontSize() { return fontSize; }
        public void setFontSize(int fontSize) { this.fontSize = fontSize; }

        public String getFontColor() { return fontColor; }
        public void setFontColor(String fontColor) { this.fontColor = fontColor; }

        public String getBackgroundColor() { return backgroundColor; }
        public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

        public int getMargin() { return margin; }
        public void setMargin(int margin) { this.margin = margin; }
    }

    /**
     * Image processing result
     */
    class ImageProcessingResult {
        private String originalFilename;
        private List<ProcessedImageVariant> variants;
        private ImageMetadata metadata;

        public ImageProcessingResult(String originalFilename, List<ProcessedImageVariant> variants, ImageMetadata metadata) {
            this.originalFilename = originalFilename;
            this.variants = variants;
            this.metadata = metadata;
        }

        // Getters and setters
        public String getOriginalFilename() { return originalFilename; }
        public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

        public List<ProcessedImageVariant> getVariants() { return variants; }
        public void setVariants(List<ProcessedImageVariant> variants) { this.variants = variants; }

        public ImageMetadata getMetadata() { return metadata; }
        public void setMetadata(ImageMetadata metadata) { this.metadata = metadata; }
    }

    /**
     * Processed image variant
     */
    class ProcessedImageVariant {
        private String sizeName;
        private int width;
        private int height;
        private String format;
        private long fileSize;
        private byte[] imageData;

        public ProcessedImageVariant(String sizeName, int width, int height, String format, long fileSize, byte[] imageData) {
            this.sizeName = sizeName;
            this.width = width;
            this.height = height;
            this.format = format;
            this.fileSize = fileSize;
            this.imageData = imageData;
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

        public byte[] getImageData() { return imageData; }
        public void setImageData(byte[] imageData) { this.imageData = imageData; }
    }

    /**
     * Image metadata
     */
    class ImageMetadata {
        private int width;
        private int height;
        private String format;
        private long fileSize;
        private int colorDepth;
        private boolean hasTransparency;

        public ImageMetadata(int width, int height, String format, long fileSize, int colorDepth, boolean hasTransparency) {
            this.width = width;
            this.height = height;
            this.format = format;
            this.fileSize = fileSize;
            this.colorDepth = colorDepth;
            this.hasTransparency = hasTransparency;
        }

        // Getters and setters
        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }

        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }

        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }

        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }

        public int getColorDepth() { return colorDepth; }
        public void setColorDepth(int colorDepth) { this.colorDepth = colorDepth; }

        public boolean isHasTransparency() { return hasTransparency; }
        public void setHasTransparency(boolean hasTransparency) { this.hasTransparency = hasTransparency; }
    }

    /**
     * Resize modes
     */
    enum ResizeMode {
        PROPORTIONAL,  // Maintain aspect ratio
        EXACT,         // Exact dimensions (may distort)
        CROP,          // Crop to fit
        FIT            // Fit within bounds
    }

    /**
     * Watermark positions
     */
    enum WatermarkPosition {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        CENTER_LEFT, CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }
}
