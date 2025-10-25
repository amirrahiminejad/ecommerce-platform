package com.webrayan.bazaar.modules.ads.repository;

import com.webrayan.bazaar.core.common.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageMetadataRepository extends JpaRepository<Image, Long> {
    Image findByFilename(String filename);
}