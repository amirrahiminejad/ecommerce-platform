package com.webrayan.bazaar.modules.catalog.service;

import com.webrayan.bazaar.modules.catalog.entity.Category;
import com.webrayan.bazaar.modules.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findAllActiveCategories();
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findActiveRootCategories();
    }

    public List<Category> getSubcategories(Long parentId) {
        return categoryRepository.findByParentIdAndIsActiveTrue(parentId);
    }

    public List<Category> getSubcategories(Category parent) {
        return categoryRepository.findActiveSubcategories(parent);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    public Category createCategory(Category category) {
        validateCategory(category);
        generateSlugIfEmpty(category);
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setSlug(categoryDetails.getSlug());
        category.setIsActive(categoryDetails.getIsActive());
        category.setSortOrder(categoryDetails.getSortOrder());
        category.setParent(categoryDetails.getParent());
        
        validateCategory(category);
        generateSlugIfEmpty(category);
        
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        // Check if category has subcategories
        List<Category> subcategories = categoryRepository.findByParentIdAndIsActiveTrue(id);
        if (!subcategories.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with subcategories");
        }
        
        // Check if category has products
        Long productCount = categoryRepository.countActiveProductsByCategory(category);
        if (productCount > 0) {
            throw new IllegalStateException("Cannot delete category with products");
        }
        
        categoryRepository.delete(category);
    }

    public void toggleCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));
        
        category.setIsActive(!category.getIsActive());
        categoryRepository.save(category);
    }

    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        
        if (categoryRepository.existsByName(category.getName()) && 
            (category.getId() == null || !categoryRepository.findById(category.getId()).get().getName().equals(category.getName()))) {
            throw new IllegalArgumentException("Category with this name already exists");
        }
        
        if (category.getSlug() != null && 
            categoryRepository.existsBySlug(category.getSlug()) && 
            (category.getId() == null || !categoryRepository.findById(category.getId()).get().getSlug().equals(category.getSlug()))) {
            throw new IllegalArgumentException("Category with this slug already exists");
        }
        
        // Prevent circular reference
        if (category.getParent() != null && category.getId() != null) {
            Category parent = category.getParent();
            while (parent != null) {
                if (parent.getId().equals(category.getId())) {
                    throw new IllegalArgumentException("Circular reference detected in category hierarchy");
                }
                parent = parent.getParent();
            }
        }
    }

    private void generateSlugIfEmpty(Category category) {
        if (category.getSlug() == null || category.getSlug().trim().isEmpty()) {
            String slug = category.getName().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", "");
            
            // Ensure slug is unique
            String originalSlug = slug;
            int counter = 1;
            while (categoryRepository.existsBySlug(slug)) {
                slug = originalSlug + "-" + counter++;
            }
            
            category.setSlug(slug);
        }
    }
}
