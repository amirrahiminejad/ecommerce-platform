package com.webrayan.commerce.modules.ads.controller;

import com.webrayan.commerce.modules.ads.entity.AdCategory;
import com.webrayan.commerce.modules.ads.service.AdCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class AdCategoryController {

    private final AdCategoryService adCategoryService;

    public AdCategoryController(AdCategoryService adCategoryService) {
        this.adCategoryService = adCategoryService;
    }

    @GetMapping
    public List<AdCategory> getAllCategories() {
        return adCategoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdCategory> getCategoryById(@PathVariable Long id) {
        return adCategoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    @PostMapping
//    public Category createCategory(@RequestBody Category category) {
//       // return categoryService.(category);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
