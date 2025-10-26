package com.webrayan.store.modules.ads.repository;

import com.webrayan.store.core.common.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageMetadataRepository extends JpaRepository<Image, Long> {
    Image findByFilename(String filename);
}