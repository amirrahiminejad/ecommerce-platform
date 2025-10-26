package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.service.UserService;
import com.webrayan.commerce.modules.ads.dto.AdRequestDto;
import com.webrayan.commerce.modules.ads.entity.Ad;
import com.webrayan.commerce.modules.ads.entity.AdImage;
import com.webrayan.commerce.modules.ads.entity.AdCategory;
import com.webrayan.commerce.core.common.entity.Image;
import com.webrayan.commerce.modules.ads.enums.AdStatus;
import com.webrayan.commerce.modules.ads.repository.AdImageRepository;
import com.webrayan.commerce.modules.ads.repository.AdRepository;
import com.webrayan.commerce.modules.ads.repository.ImageMetadataRepository;
import com.webrayan.commerce.core.common.entity.Location;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdImageRepository adImageRepository;
    private final ImageMetadataRepository imageMetadataRepository;
    private final UserService userService;
    private final String UPLOAD_DIR = "uploads/";
    
    @Autowired
    private LogService logService;

    @Override
    public Ad createAd(AdRequestDto adRequest) {
        validateAd(adRequest);
        Ad ad = new Ad();
        ad.setTitle(adRequest.getTitle());
        ad.setCategory(new AdCategory(adRequest.getCategoryId()));
        ad.setDescription(adRequest.getDescription());
        ad.setPrice(adRequest.getPrice());
        
        // Set location by ID
        if (adRequest.getLocationId() != null) {
            Location location = new Location();
            location.setId(adRequest.getLocationId().intValue()); // Convert Long to Integer
            ad.setLocation(location);
        }
        
        ad.setNegotiable(adRequest.getNegotiable());
        ad.setStatus(AdStatus.PENDING);
        
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("کاربر وارد سیستم نشده است");
        }
        
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("کاربر فعلی یافت نشد"));
        
        ad.setUser(currentUser);
        Ad saved = adRepository.save(ad);
        logService.log("AdService", "createAd", "Ad created with id: " + saved.getId());
        return saved;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Ad createAd(AdRequestDto adRequest, org.springframework.web.multipart.MultipartFile[] images) {
        // ایجاد آگهی اصلی
        Ad ad = createAd(adRequest);
        
        // پردازش تصاویر
        if (images != null && images.length > 0) {
            for (org.springframework.web.multipart.MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        saveAdImage(ad, image);
                    } catch (Exception e) {
                        logService.log("AdService", "createAd", "Error saving image for ad " + ad.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
        
        return ad;
    }
    
    @org.springframework.transaction.annotation.Transactional
    protected void saveAdImage(Ad ad, org.springframework.web.multipart.MultipartFile imageFile) throws Exception {
        // ایجاد نام فایل منحصر به فرد
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : ".jpg";
        String filename = "ad_" + ad.getId() + "_" + System.currentTimeMillis() + extension;
        
        // ذخیره فایل در دیسک
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(UPLOAD_DIR);
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }
        
        java.nio.file.Path filePath = uploadPath.resolve(filename);
        java.nio.file.Files.copy(imageFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        // ایجاد رکورد Image در دیتابیس
        com.webrayan.commerce.core.common.entity.Image image = new com.webrayan.commerce.core.common.entity.Image();
        image.setFilename(filename);
        image.setOriginalFilename(originalFilename);
        image.setSize(String.valueOf(imageFile.getSize()));
        image.setUploadDate(java.time.LocalDateTime.now());
        image.setUrl("/uploads/" + filename);
        
        // ذخیره Image در repository و دریافت saved instance
        com.webrayan.commerce.core.common.entity.Image savedImage = imageMetadataRepository.save(image);
        
        // ایجاد رابطه AdImage
        com.webrayan.commerce.modules.ads.entity.AdImage adImage = new com.webrayan.commerce.modules.ads.entity.AdImage();
        adImage.setAd(ad);
        adImage.setImage(savedImage); // استفاده از saved instance
        adImageRepository.save(adImage);
    }

    @Override
    public Ad updateAd(Long id, AdRequestDto adRequestDto) {
        Ad existingAd = adRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found with id: " + id));
        validateAd(adRequestDto);
        existingAd.setTitle(adRequestDto.getTitle());
        existingAd.setDescription(adRequestDto.getDescription());
        existingAd.setPrice(adRequestDto.getPrice());
        existingAd.setCategory(new AdCategory(adRequestDto.getCategoryId()));
        // Set location by ID
        if (adRequestDto.getLocationId() != null) {
            Location location = new Location();
            location.setId(adRequestDto.getLocationId().intValue()); // Convert Long to Integer
            existingAd.setLocation(location);
        }
        // در ویرایش وضعیت آگهی Pending میشود
        existingAd.setStatus(AdStatus.PENDING);
        Ad saved = adRepository.save(existingAd);
        logService.log("AdService", "updateAd", "Ad updated with id: " + saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Ad changeStatus(Long id, AdStatus status, String reason) {
        // Use findById without loading collections to avoid ConcurrentModificationException
        Ad ad = adRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found with id: " + id));
        
        // Set status and reason
        ad.setStatus(status);
        if (status == AdStatus.REJECTED) {
            ad.setRejectionReason(reason);
        }
        
        // Set review date
        ad.setReviewedAt(new Date());
        
        // Save the entity within the transaction
        Ad saved = adRepository.save(ad);
        
        // Flush to ensure changes are persisted
        adRepository.flush();
        
        logService.log("AdService", "changeStatus", "Status changed for ad id: " + id + " to " + status);
        return saved;
    }

    @Override
    public Ad getAdById(Long id) {
        Ad ad = adRepository.findByIdWithImages(id);
        if (ad == null) {
            throw new EntityNotFoundException("Ad not found with id: " + id);
        }
        logService.log("AdService", "getAdById", "Fetched ad with id: " + id);
        return ad;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Ad updateAdWithImages(Long id, AdRequestDto adRequest, org.springframework.web.multipart.MultipartFile[] images, java.util.List<Long> keepImages) {
        // ویرایش آگهی اصلی
        Ad updatedAd = updateAd(id, adRequest);
        
        // حذف تصاویری که نباید نگهداری شوند
        if (updatedAd.getImages() != null) {
            java.util.List<com.webrayan.commerce.modules.ads.entity.AdImage> imagesToRemove = new java.util.ArrayList<>();
            for (com.webrayan.commerce.modules.ads.entity.AdImage adImage : updatedAd.getImages()) {
                if (keepImages == null || !keepImages.contains(adImage.getImage().getId())) {
                    imagesToRemove.add(adImage);
                }
            }
            
            // حذف تصاویر از database و فایل سیستم
            for (com.webrayan.commerce.modules.ads.entity.AdImage adImage : imagesToRemove) {
                try {
                    // حذف فایل از دیسک
                    java.nio.file.Path filePath = java.nio.file.Paths.get(UPLOAD_DIR + adImage.getImage().getFilename());
                    if (java.nio.file.Files.exists(filePath)) {
                        java.nio.file.Files.delete(filePath);
                    }
                    
                    // حذف از database
                    adImageRepository.delete(adImage);
                    imageMetadataRepository.delete(adImage.getImage());
                } catch (Exception e) {
                    logService.log("AdService", "updateAdWithImages", "Error deleting image: " + e.getMessage());
                }
            }
        }
        
        // اضافه کردن تصاویر جدید
        if (images != null && images.length > 0) {
            for (org.springframework.web.multipart.MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        saveAdImage(updatedAd, image);
                    } catch (Exception e) {
                        logService.log("AdService", "updateAdWithImages", "Error saving new image: " + e.getMessage());
                    }
                }
            }
        }
        
        return updatedAd;
    }

    @Override
    public List<Ad> getAdsByStatus(AdStatus status) {
        List<Ad> ads = adRepository.findByStatus(status);
        logService.log("AdService", "getAdsByStatus", "Fetched ads with status: " + status);
        return ads;
    }

    @Override
    public List<Ad> getAdsByUser(Long userId) {
        List<Ad> ads = adRepository.findByUser_Id(userId);
        logService.log("AdService", "getAdsByUser", "Fetched ads for user id: " + userId);
        return ads;
    }

    @Override
    public void deleteAd(Long id) {
        if (!adRepository.existsById(id)) {
            throw new EntityNotFoundException("Ad not found with id: " + id);
        }
        adRepository.deleteById(id);
        logService.log("AdService", "deleteAd", "Deleted ad with id: " + id);
    }

    private void validateAd(AdRequestDto ad) {
        if (ad.getTitle() == null || ad.getTitle().isBlank()) {
            throw new IllegalArgumentException("Ad title is required");
        }
        if (ad.getPrice() != null && ad.getPrice() < 0) {
            throw new IllegalArgumentException("Ad price cannot be negative");
        }
        if (ad.getCategoryId() == null) {
            throw new IllegalArgumentException("Category is required for the ad");
        }
//        if (ad.getLocation() == null) {
//            throw new IllegalArgumentException("Location is required for the ad");
//        }
    }

    public void uploadAdImage(Long adId, MultipartFile file) throws IOException {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new EntityNotFoundException("Ad not found with id: " + adId));
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid file");
        }
        // ذخیره تصویر اصلی
        String originalFilePath = UPLOAD_DIR + "original_" + originalFilename;
        file.transferTo(new File(originalFilePath));
        // سایزهای استاندارد
        int[][] sizes = {{150, 150}, {300, 300}, {600, 600}};
        for (int[] size : sizes) {
            String outputFilePath = UPLOAD_DIR + size[0] + "x" + size[1] + "_" + originalFilename;
            net.coobird.thumbnailator.Thumbnails.of(file.getInputStream())
                    .size(size[0], size[1])
                    .toFile(outputFilePath);
        }
        // ذخیره اطلاعات تصویر
        Image image = new Image();
        image.setFilename(originalFilename);
        image.setOriginalFilename(originalFilename);
        image.setSize("original");
        image.setUploadDate(LocalDateTime.now());
        imageMetadataRepository.save(image);
        // ایجاد ارتباط با آگهی
        AdImage adImage = new AdImage();
        adImage.setAd(ad);
        adImage.setImage(image);
        adImageRepository.save(adImage);
        logService.log("AdService", "uploadAdImage", "Image uploaded for ad id: " + adId);
    }

    @Override
    public long count() {
        return adRepository.count();
    }

    @Override
    public long countByStatus(AdStatus status) {
        return adRepository.countByStatus(status);
    }
    
    // New methods for home page
    
    @Override
    public Page<Ad> getApprovedAds(Pageable pageable) {
        return adRepository.findByStatusOrderByCreatedAtDesc(AdStatus.APPROVED, pageable);
    }
    
    @Override
    public Page<Ad> searchAds(String searchTerm, Pageable pageable) {
        return adRepository.findByStatusAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            AdStatus.APPROVED, searchTerm, pageable);
    }
    
    @Override
    public Page<Ad> getAdsByCategory(AdCategory category, Pageable pageable) {
        return adRepository.findByStatusAndCategoryOrderByCreatedAtDesc(
            AdStatus.APPROVED, category, pageable);
    }
    
    @Override
    public Page<Ad> getAdsByLocation(Location location, Pageable pageable) {
        return adRepository.findByStatusAndLocationOrderByCreatedAtDesc(
            AdStatus.APPROVED, location, pageable);
    }
    
    @Override
    public Page<Ad> getAdsByCategoryAndLocation(AdCategory category, Location location, Pageable pageable) {
        return adRepository.findByStatusAndCategoryAndLocationOrderByCreatedAtDesc(
            AdStatus.APPROVED, category, location, pageable);
    }
    
    @Override
    public Page<Ad> searchAdsByCategoryAndTitle(AdCategory category, String searchTerm, Pageable pageable) {
        return adRepository.findByStatusAndCategoryAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            AdStatus.APPROVED, category, searchTerm, pageable);
    }
    
    @Override
    public Page<Ad> searchAdsByLocationAndTitle(Location location, String searchTerm, Pageable pageable) {
        return adRepository.findByStatusAndLocationAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            AdStatus.APPROVED, location, searchTerm, pageable);
    }
    
    @Override
    public List<Ad> getRelatedAds(Ad ad, int limit) {
        if (ad.getCategory() != null) {
            return adRepository.findByStatusAndCategoryAndIdNotOrderByCreatedAtDesc(
                AdStatus.APPROVED, ad.getCategory(), ad.getId(), PageRequest.of(0, limit)).getContent();
        } else {
            return adRepository.findByStatusAndIdNotOrderByCreatedAtDesc(
                AdStatus.APPROVED, ad.getId(), PageRequest.of(0, limit)).getContent();
        }
    }
    
    // Profile related methods implementation
    
    @Override
    public long countByUser(User user) {
        return adRepository.countByUser(user);
    }
    
    @Override
    public long countActiveAdsByUser(User user) {
        return adRepository.countByUserAndStatusAndIsActive(user, AdStatus.APPROVED, true);
    }
    
    @Override
    public Page<Ad> getAdsByUser(User user, Pageable pageable) {
        return adRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Override
    public Page<Ad> getAdsByUserAndStatus(User user, String status, Pageable pageable) {
        try {
            AdStatus adStatus = AdStatus.valueOf(status.toUpperCase());
            return adRepository.findByUserAndStatusOrderByCreatedAtDesc(user, adStatus, pageable);
        } catch (IllegalArgumentException e) {
            // If invalid status, return all user ads
            return adRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        }
    }

    @Override
    public long countByUserAndStatus(User user, AdStatus status) {
        return adRepository.countByUserAndStatus(user, status);
    }

    @Override
    public void incrementViewCount(Long adId) {
        Ad ad = adRepository.findById(adId)
            .orElseThrow(() -> new EntityNotFoundException("آگهی با شناسه " + adId + " یافت نشد"));
        
        ad.setViewsCount(ad.getViewsCount() + 1);
        adRepository.save(ad);
    }

    @Override
    public boolean isAdOwner(Long adId, String username) {
        if (username == null || adId == null) {
            return false;
        }
        
        return adRepository.findById(adId)
                .map(ad -> ad.getUser().getUsername().equals(username))
                .orElse(false);
    }
}
