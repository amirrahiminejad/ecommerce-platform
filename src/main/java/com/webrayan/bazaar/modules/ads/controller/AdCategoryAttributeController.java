package com.webrayan.bazaar.modules.ads.controller;

import com.webrayan.bazaar.modules.ads.entity.AdCategoryAttribute;
import com.webrayan.bazaar.modules.ads.service.AdCategoryAttributeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/category-attributes")
public class AdCategoryAttributeController {

    private final AdCategoryAttributeService adCategoryAttributeService;

    public AdCategoryAttributeController(AdCategoryAttributeService adCategoryAttributeService) {
        this.adCategoryAttributeService = adCategoryAttributeService;
    }

    @GetMapping
    public List<AdCategoryAttribute> getAllAttributes() {
        return adCategoryAttributeService.getAllAttributes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdCategoryAttribute> getAttributeById(@PathVariable Long id) {
        return adCategoryAttributeService.getAttributeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AdCategoryAttribute createAttribute(@RequestBody AdCategoryAttribute attribute) {
        return adCategoryAttributeService.saveAttribute(attribute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        adCategoryAttributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }
}
