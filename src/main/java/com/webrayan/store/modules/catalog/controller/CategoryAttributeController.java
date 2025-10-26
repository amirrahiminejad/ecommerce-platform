package com.webrayan.store.modules.catalog.controller;

import com.webrayan.store.modules.catalog.entity.CategoryAttribute;
import com.webrayan.store.modules.catalog.service.CategoryAttributeService;
import com.webrayan.store.modules.catalog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/catalog/category-attributes")
@RequiredArgsConstructor
public class CategoryAttributeController {

    private final CategoryAttributeService categoryAttributeService;
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryAttribute>> getAllAttributes() {
        List<CategoryAttribute> attributes = categoryAttributeService.getAllAttributes();
        return ResponseEntity.ok(attributes);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CategoryAttribute>> getAttributesByCategory(@PathVariable Long categoryId) {
        List<CategoryAttribute> attributes = categoryAttributeService.getAttributesByCategoryId(categoryId);
        return ResponseEntity.ok(attributes);
    }

    @GetMapping("/category/{categoryId}/required")
    public ResponseEntity<List<CategoryAttribute>> getRequiredAttributesByCategory(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId)
                .map(category -> {
                    List<CategoryAttribute> attributes = categoryAttributeService.getRequiredAttributesByCategory(category);
                    return ResponseEntity.ok(attributes);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}/filterable")
    public ResponseEntity<List<CategoryAttribute>> getFilterableAttributesByCategory(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId)
                .map(category -> {
                    List<CategoryAttribute> attributes = categoryAttributeService.getFilterableAttributesByCategory(category);
                    return ResponseEntity.ok(attributes);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}/searchable")
    public ResponseEntity<List<CategoryAttribute>> getSearchableAttributesByCategory(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId)
                .map(category -> {
                    List<CategoryAttribute> attributes = categoryAttributeService.getSearchableAttributesByCategory(category);
                    return ResponseEntity.ok(attributes);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryAttribute> getAttributeById(@PathVariable Long id) {
        return categoryAttributeService.getAttributeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryAttribute> createAttribute(@RequestBody CategoryAttribute attribute) {
        try {
            CategoryAttribute createdAttribute = categoryAttributeService.createAttribute(attribute);
            return ResponseEntity.ok(createdAttribute);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryAttribute> updateAttribute(@PathVariable Long id, @RequestBody CategoryAttribute attribute) {
        try {
            CategoryAttribute updatedAttribute = categoryAttributeService.updateAttribute(id, attribute);
            return ResponseEntity.ok(updatedAttribute);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        try {
            categoryAttributeService.deleteAttribute(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleAttributeStatus(@PathVariable Long id) {
        try {
            categoryAttributeService.toggleAttributeStatus(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
