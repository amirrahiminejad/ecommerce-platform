package com.webrayan.bazaar.core.service.impl;

import com.webrayan.bazaar.core.config.FileUploadConfig;
import com.webrayan.bazaar.core.service.ImageProcessingService;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessingServiceImpl.class);

    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    public ImageProcessingResult processImage(MultipartFile file, ImageProcessingOptions options) throws IOException {
        logger.info("Processing image: {} with {} size variants", file.getOriginalFilename(), 
                   options.getSizes() != null ? options.getSizes().size() : 0);

        byte[] originalImageData = file.getBytes();
        ImageMetadata metadata = extractMetadata(originalImageData);
        
        List<ProcessedImageVariant> variants = new ArrayList<>();

        // Process each size variant
        if (options.getSizes() != null) {
            for (ImageSize size : options.getSizes()) {
                try {
                    byte[] resizedImage = resizeImage(originalImageData, size.getWidth(), size.getHeight(), options.getResizeMode());
                    
                    // Apply watermark if enabled
                    if (options.isEnableWatermark() && options.getWatermarkOptions() != null) {
                        resizedImage = addWatermark(resizedImage, options.getWatermarkOptions());
                    }

                    // Optimize image
                    String format = determineOptimalFormat(file.getContentType(), options.isEnableWebpConversion());
                    resizedImage = optimizeImage(resizedImage, format, options.getJpegQuality());

                    // Get actual dimensions after processing
                    BufferedImage processedImg = ImageIO.read(new ByteArrayInputStream(resizedImage));
                    
                    variants.add(new ProcessedImageVariant(
                        size.getName(),
                        processedImg.getWidth(),
                        processedImg.getHeight(),
                        format,
                        resizedImage.length,
                        resizedImage
                    ));

                    logger.debug("Created variant: {} ({}x{}, {} bytes)", 
                               size.getName(), processedImg.getWidth(), processedImg.getHeight(), resizedImage.length);

                } catch (Exception e) {
                    logger.error("Failed to process size variant: {}", size.getName(), e);
                    throw new IOException("Failed to process image size: " + size.getName(), e);
                }
            }
        }

        // Add original as a variant if not already included
        boolean hasOriginal = variants.stream().anyMatch(v -> "original".equals(v.getSizeName()));
        if (!hasOriginal) {
            byte[] originalProcessed = originalImageData;
            
            if (options.isEnableWatermark() && options.getWatermarkOptions() != null) {
                originalProcessed = addWatermark(originalProcessed, options.getWatermarkOptions());
            }

            String format = determineOptimalFormat(file.getContentType(), options.isEnableWebpConversion());
            originalProcessed = optimizeImage(originalProcessed, format, options.getJpegQuality());

            variants.add(new ProcessedImageVariant(
                "original",
                metadata.getWidth(),
                metadata.getHeight(),
                format,
                originalProcessed.length,
                originalProcessed
            ));
        }

        return new ImageProcessingResult(file.getOriginalFilename(), variants, metadata);
    }

    @Override
    public List<ImageProcessingResult> processImages(List<MultipartFile> files, ImageProcessingOptions options) throws IOException {
        List<ImageProcessingResult> results = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                ImageProcessingResult result = processImage(file, options);
                results.add(result);
            } catch (Exception e) {
                logger.error("Failed to process image: {}", file.getOriginalFilename(), e);
                throw new IOException("Failed to process image: " + file.getOriginalFilename(), e);
            }
        }
        
        return results;
    }

    @Override
    public byte[] resizeImage(byte[] imageData, int width, int height, ResizeMode mode) throws IOException {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
            
            Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(originalImage);
            
            switch (mode) {
                case PROPORTIONAL:
                    builder.size(width, height).keepAspectRatio(true);
                    break;
                case EXACT:
                    builder.size(width, height).keepAspectRatio(false);
                    break;
                case CROP:
                    builder.size(width, height).crop(Positions.CENTER);
                    break;
                case FIT:
                    builder.size(width, height).keepAspectRatio(true);
                    break;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            builder.outputFormat("jpg").toOutputStream(outputStream);
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Failed to resize image to {}x{}", width, height, e);
            throw new IOException("Failed to resize image", e);
        }
    }

    @Override
    public byte[] convertFormat(byte[] imageData, String sourceFormat, String targetFormat) throws IOException {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            
            // Handle transparency for formats that don't support it
            if ("jpg".equalsIgnoreCase(targetFormat) || "jpeg".equalsIgnoreCase(targetFormat)) {
                BufferedImage jpegImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                jpegImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);
                image = jpegImage;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, targetFormat.toLowerCase(), outputStream);
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Failed to convert image from {} to {}", sourceFormat, targetFormat, e);
            throw new IOException("Failed to convert image format", e);
        }
    }

    @Override
    public byte[] addWatermark(byte[] imageData, WatermarkOptions watermarkOptions) throws IOException {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            Graphics2D g2d = image.createGraphics();

            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Set font
            Font font = new Font(Font.SANS_SERIF, Font.BOLD, watermarkOptions.getFontSize());
            g2d.setFont(font);

            // Calculate text dimensions
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(watermarkOptions.getText());
            int textHeight = fontMetrics.getHeight();

            // Calculate position
            Point position = calculateWatermarkPosition(
                image.getWidth(), image.getHeight(),
                textWidth, textHeight,
                watermarkOptions.getPosition(),
                watermarkOptions.getMargin()
            );

            // Set transparency
            AlphaComposite alphaComposite = AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, watermarkOptions.getOpacity()
            );
            g2d.setComposite(alphaComposite);

            // Draw background if specified
            if (watermarkOptions.getBackgroundColor() != null) {
                g2d.setColor(Color.decode(watermarkOptions.getBackgroundColor()));
                g2d.fillRect(
                    position.x - 5, 
                    position.y - textHeight + 5,
                    textWidth + 10, 
                    textHeight
                );
            }

            // Draw text
            g2d.setColor(Color.decode(watermarkOptions.getFontColor()));
            g2d.drawString(watermarkOptions.getText(), position.x, position.y);

            g2d.dispose();

            // Convert back to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Failed to add watermark to image", e);
            throw new IOException("Failed to add watermark", e);
        }
    }

    @Override
    public byte[] optimizeImage(byte[] imageData, String format, float quality) throws IOException {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
                // Use JPEG compression with quality control
                Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                if (writers.hasNext()) {
                    ImageWriter writer = writers.next();
                    ImageWriteParam param = writer.getDefaultWriteParam();
                    
                    if (param.canWriteCompressed()) {
                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(quality);
                    }
                    
                    ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
                    writer.setOutput(ios);
                    writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
                    
                    writer.dispose();
                    ios.close();
                } else {
                    ImageIO.write(image, "jpg", outputStream);
                }
            } else {
                ImageIO.write(image, format, outputStream);
            }
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Failed to optimize image", e);
            throw new IOException("Failed to optimize image", e);
        }
    }

    @Override
    public byte[] generateThumbnail(byte[] imageData, int size) throws IOException {
        return resizeImage(imageData, size, size, ResizeMode.CROP);
    }

    @Override
    public ImageMetadata extractMetadata(byte[] imageData) throws IOException {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            
            if (image == null) {
                throw new IOException("Invalid image data");
            }

            int width = image.getWidth();
            int height = image.getHeight();
            int colorDepth = image.getColorModel().getPixelSize();
            boolean hasTransparency = image.getColorModel().hasAlpha();
            
            // Determine format from image data
            String format = "unknown";
            try {
                // Simple format detection based on header bytes
                if (imageData.length >= 4) {
                    if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
                        format = "jpeg";
                    } else if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50) {
                        format = "png";
                    } else if (imageData[0] == (byte) 0x47 && imageData[1] == (byte) 0x49) {
                        format = "gif";
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to detect image format", e);
            }

            return new ImageMetadata(width, height, format, imageData.length, colorDepth, hasTransparency);
            
        } catch (Exception e) {
            logger.error("Failed to extract image metadata", e);
            throw new IOException("Failed to extract image metadata", e);
        }
    }

    /**
     * Determine optimal format based on original format and configuration
     */
    private String determineOptimalFormat(String originalContentType, boolean enableWebpConversion) {
        if (enableWebpConversion && !"image/gif".equals(originalContentType)) {
            // Convert to WebP for better compression (except GIFs to preserve animation)
            return "webp";
        }
        
        if ("image/jpeg".equals(originalContentType) || "image/jpg".equals(originalContentType)) {
            return "jpg";
        } else if ("image/png".equals(originalContentType)) {
            return "png";
        } else if ("image/gif".equals(originalContentType)) {
            return "gif";
        } else if ("image/webp".equals(originalContentType)) {
            return "webp";
        }
        
        return "jpg"; // Default fallback
    }

    /**
     * Calculate watermark position based on image dimensions and position enum
     */
    private Point calculateWatermarkPosition(int imageWidth, int imageHeight, int textWidth, int textHeight,
                                           WatermarkPosition position, int margin) {
        int x, y;
        
        switch (position) {
            case TOP_LEFT:
                x = margin;
                y = margin + textHeight;
                break;
            case TOP_CENTER:
                x = (imageWidth - textWidth) / 2;
                y = margin + textHeight;
                break;
            case TOP_RIGHT:
                x = imageWidth - textWidth - margin;
                y = margin + textHeight;
                break;
            case CENTER_LEFT:
                x = margin;
                y = (imageHeight + textHeight) / 2;
                break;
            case CENTER:
                x = (imageWidth - textWidth) / 2;
                y = (imageHeight + textHeight) / 2;
                break;
            case CENTER_RIGHT:
                x = imageWidth - textWidth - margin;
                y = (imageHeight + textHeight) / 2;
                break;
            case BOTTOM_LEFT:
                x = margin;
                y = imageHeight - margin;
                break;
            case BOTTOM_CENTER:
                x = (imageWidth - textWidth) / 2;
                y = imageHeight - margin;
                break;
            case BOTTOM_RIGHT:
            default:
                x = imageWidth - textWidth - margin;
                y = imageHeight - margin;
                break;
        }
        
        return new Point(x, y);
    }
}
