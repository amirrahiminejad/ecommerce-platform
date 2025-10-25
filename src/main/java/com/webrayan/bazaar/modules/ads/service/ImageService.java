package com.webrayan.bazaar.modules.ads.service;

import com.webrayan.bazaar.core.common.entity.Image;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ImageService {
    String uploadImage(MultipartFile file, Long adId) throws IOException;
    byte[] viewImage(String size, String filename) throws IOException;
    boolean deleteImage(String filename, Long adId) throws IOException;
    List<Image> listImagesByAd(Long adId);
}
