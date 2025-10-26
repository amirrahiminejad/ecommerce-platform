package com.webrayan.commerce.modules.ads.repository;

import com.webrayan.commerce.core.common.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageMetadataRepository extends JpaRepository<Image, Long> {
    Image findByFilename(String filename);
}