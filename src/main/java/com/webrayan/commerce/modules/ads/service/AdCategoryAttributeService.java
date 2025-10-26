package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.modules.ads.entity.AdCategoryAttribute;
import com.webrayan.commerce.modules.ads.repository.AdCategoryAttributeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdCategoryAttributeService {

    private final AdCategoryAttributeRepository adCategoryAttributeRepository;

    public AdCategoryAttributeService(AdCategoryAttributeRepository adCategoryAttributeRepository) {
        this.adCategoryAttributeRepository = adCategoryAttributeRepository;
    }

    public List<AdCategoryAttribute> getAllAttributes() {
        return adCategoryAttributeRepository.findAll();
    }

    public Optional<AdCategoryAttribute> getAttributeById(Long id) {
        return adCategoryAttributeRepository.findById(id);
    }

    public AdCategoryAttribute saveAttribute(AdCategoryAttribute attribute) {
        return adCategoryAttributeRepository.save(attribute);
    }

    public void deleteAttribute(Long id) {
        adCategoryAttributeRepository.deleteById(id);
    }
}
