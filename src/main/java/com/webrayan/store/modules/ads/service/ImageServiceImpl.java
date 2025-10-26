package com.webrayan.store.modules.ads.service;

import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.ads.entity.AdImage;
import com.webrayan.store.core.common.entity.Image;
import com.webrayan.store.modules.ads.repository.ImageMetadataRepository;
import com.webrayan.store.modules.ads.repository.AdImageRepository;
import com.webrayan.store.modules.ads.repository.AdRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    private final String UPLOAD_DIR = "uploads/";
    @Autowired
    private ImageMetadataRepository imageMetadataRepository;
    @Autowired
    private AdRepository adRepository;
    @Autowired
    private AdImageRepository adImageRepository;

    public ImageServiceImpl() {
        new File(UPLOAD_DIR).mkdirs();
    }

    @Override
    public String uploadImage(MultipartFile file, Long adId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid file");
        }
        String originalFilePath = UPLOAD_DIR + "original_" + originalFilename;
        file.transferTo(new File(originalFilePath));
        int[][] sizes = {{150, 150}, {300, 300}, {600, 600}};
        for (int[] size : sizes) {
            String outputFilePath = UPLOAD_DIR + size[0] + "x" + size[1] + "_" + originalFilename;
            Thumbnails.of(file.getInputStream())
                    .size(size[0], size[1])
                    .toFile(outputFilePath);
        }
        Image image = new Image();
        image.setFilename(originalFilename);
        image.setOriginalFilename(originalFilename);
        image.setSize("original");
        image.setUploadDate(LocalDateTime.now());
        imageMetadataRepository.save(image);
        if (adId != null) {
            Optional<Ad> adOpt = adRepository.findById(adId);
            if (adOpt.isPresent()) {
                AdImage adImage = new AdImage();
                adImage.setAd(adOpt.get());
                adImage.setImage(image);
                adImageRepository.save(adImage);
            }
        }
        return "Image uploaded successfully" + (adId != null ? " and linked to ad." : ".");
    }

    @Override
    public byte[] viewImage(String size, String filename) throws IOException {
        String imagePath;
        if (size.equals("original")) {
            imagePath = UPLOAD_DIR + "original_" + filename;
        } else {
            imagePath = UPLOAD_DIR + size + "_" + filename;
        }
        Path path = Paths.get(imagePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found");
        }
        return Files.readAllBytes(path);
    }

    @Override
    public boolean deleteImage(String filename, Long adId) throws IOException {
        boolean deleted = false;
        String[] sizes = {"original", "150x150", "300x300", "600x600"};
        for (String size : sizes) {
            String filePath = size.equals("original") ? UPLOAD_DIR + "original_" + filename : UPLOAD_DIR + size + "_" + filename;
            File file = new File(filePath);
            if (file.exists()) {
                deleted |= file.delete();
            }
        }
        Image image = imageMetadataRepository.findByFilename(filename);
        if (image != null) {
            imageMetadataRepository.delete(image);
        }
        if (adId != null) {
            Optional<Ad> adOpt = adRepository.findById(adId);
            if (adOpt.isPresent()) {
                adImageRepository.deleteByAdAndImage(adOpt.get(), image);
            }
        }
        return deleted;
    }

    @Override
    public List<Image> listImagesByAd(Long adId) {
        List<Image> images = new ArrayList<>();
        Optional<Ad> adOpt = adRepository.findById(adId);
        if (adOpt.isPresent()) {
            List<AdImage> adImages = adImageRepository.findByAd(adOpt.get());
            for (AdImage adImage : adImages) {
                images.add(adImage.getImage());
            }
        }
        return images;
    }
}
