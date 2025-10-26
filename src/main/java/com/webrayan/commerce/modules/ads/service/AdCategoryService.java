package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.modules.ads.entity.AdCategory;
import com.webrayan.commerce.modules.ads.repository.AdCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdCategoryService {

    private final AdCategoryRepository adCategoryRepository;

    @Autowired
    private LogService logService;

    public AdCategoryService(AdCategoryRepository adCategoryRepository) {
        this.adCategoryRepository = adCategoryRepository;
    }

    public List<AdCategory> getAllCategories() {
        List<AdCategory> list = adCategoryRepository.findAll();
        logService.log("CategoryService", "getAllCategories", "Fetched all categories");
        return list;
    }

    public Optional<AdCategory> getCategoryById(Long id) {
        Optional<AdCategory> cat = adCategoryRepository.findById(id);
        logService.log("CategoryService", "getCategoryById", "Fetched category id: " + id);
        return cat;
    }

    public AdCategory createCategory(AdCategory adCategory) {
        if (adCategoryRepository.existsByName(adCategory.getName())) {
            logService.log("CategoryService", "createCategory", "Duplicate category name: " + adCategory.getName());
            throw new IllegalArgumentException("Category with the same name already exists.");
        }
        AdCategory saved = adCategoryRepository.save(adCategory);
        logService.log("CategoryService", "createCategory", "Created category id: " + saved.getId());
        return saved;
    }

//    public List<Category> getSubcategories(Long parentId) {
//        return categoryRepository.findByParentCategory(new Category(parentId));
//    }

    public void deleteCategory(Long id) {
        adCategoryRepository.deleteById(id);
        logService.log("CategoryService", "deleteCategory", "Deleted category id: " + id);
    }
}