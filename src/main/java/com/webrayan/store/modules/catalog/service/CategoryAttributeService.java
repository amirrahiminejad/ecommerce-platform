package com.webrayan.store.modules.catalog.service;

import com.webrayan.store.modules.catalog.entity.CategoryAttribute;
import com.webrayan.store.modules.catalog.entity.Category;
import com.webrayan.store.modules.catalog.repository.CategoryAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryAttributeService {

    private final CategoryAttributeRepository categoryAttributeRepository;

    public List<CategoryAttribute> getAllAttributes() {
        return categoryAttributeRepository.findAll();
    }

    public List<CategoryAttribute> getAttributesByCategory(Category category) {
        return categoryAttributeRepository.findByCategoryAndIsActiveTrueOrderBySortOrderAsc(category);
    }

    public List<CategoryAttribute> getAttributesByCategoryId(Long categoryId) {
        return categoryAttributeRepository.findByCategoryIdAndIsActiveTrueOrderBySortOrderAsc(categoryId);
    }

    public List<CategoryAttribute> getRequiredAttributesByCategory(Category category) {
        return categoryAttributeRepository.findRequiredAttributesByCategory(category);
    }

    public List<CategoryAttribute> getFilterableAttributesByCategory(Category category) {
        return categoryAttributeRepository.findFilterableAttributesByCategory(category);
    }

    public List<CategoryAttribute> getSearchableAttributesByCategory(Category category) {
        return categoryAttributeRepository.findSearchableAttributesByCategory(category);
    }

    public Optional<CategoryAttribute> getAttributeById(Long id) {
        return categoryAttributeRepository.findById(id);
    }

    public CategoryAttribute createAttribute(CategoryAttribute attribute) {
        validateAttribute(attribute);
        return categoryAttributeRepository.save(attribute);
    }

    public CategoryAttribute updateAttribute(Long id, CategoryAttribute attributeDetails) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + id));
        
        attribute.setName(attributeDetails.getName());
        attribute.setDescription(attributeDetails.getDescription());
        attribute.setType(attributeDetails.getType());
        attribute.setIsRequired(attributeDetails.getIsRequired());
        attribute.setIsFilterable(attributeDetails.getIsFilterable());
        attribute.setIsSearchable(attributeDetails.getIsSearchable());
        attribute.setSortOrder(attributeDetails.getSortOrder());
        attribute.setIsActive(attributeDetails.getIsActive());
        attribute.setDefaultValue(attributeDetails.getDefaultValue());
        attribute.setValidationRules(attributeDetails.getValidationRules());
        attribute.setOptions(attributeDetails.getOptions());
        
        validateAttribute(attribute);
        
        return categoryAttributeRepository.save(attribute);
    }

    public void deleteAttribute(Long id) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + id));
        
        // Check if attribute has values (products using it)
        if (!attribute.getAttributeValues().isEmpty()) {
            throw new IllegalStateException("Cannot delete attribute that is being used by products");
        }
        
        categoryAttributeRepository.delete(attribute);
    }

    public void toggleAttributeStatus(Long id) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + id));
        
        attribute.setIsActive(!attribute.getIsActive());
        categoryAttributeRepository.save(attribute);
    }

    private void validateAttribute(CategoryAttribute attribute) {
        if (attribute.getName() == null || attribute.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Attribute name cannot be empty");
        }
        
        if (attribute.getCategory() == null) {
            throw new IllegalArgumentException("Attribute must belong to a category");
        }
        
        if (attribute.getType() == null) {
            throw new IllegalArgumentException("Attribute type cannot be null");
        }
        
        // Check for duplicate names within the same category
        if (categoryAttributeRepository.existsByCategoryAndName(attribute.getCategory(), attribute.getName()) &&
            (attribute.getId() == null || !categoryAttributeRepository.findById(attribute.getId()).get().getName().equals(attribute.getName()))) {
            throw new IllegalArgumentException("Attribute with this name already exists in this category");
        }
        
        // Set default values
        if (attribute.getIsRequired() == null) {
            attribute.setIsRequired(false);
        }
        if (attribute.getIsFilterable() == null) {
            attribute.setIsFilterable(false);
        }
        if (attribute.getIsSearchable() == null) {
            attribute.setIsSearchable(false);
        }
        if (attribute.getIsActive() == null) {
            attribute.setIsActive(true);
        }
        if (attribute.getSortOrder() == null) {
            attribute.setSortOrder(0);
        }
    }
}
