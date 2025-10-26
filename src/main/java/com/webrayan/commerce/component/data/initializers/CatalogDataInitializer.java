package com.webrayan.commerce.component.data.initializers;

import com.webrayan.commerce.modules.catalog.entity.Category;
import com.webrayan.commerce.modules.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogDataInitializer {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void initialize() {
        log.info("📦 Starting catalog data initialization...");
        
        initializeMainCategories();
        initializeElectronicsSubCategories();
        
        log.info("✅ Catalog data initialized successfully");
    }

    private void initializeMainCategories() {
        log.info("📂 Creating main categories...");
        
        List<String[]> mainCategories = Arrays.asList(
            new String[]{"Electronics", "Mobile phones, laptops, tablets and other electronic devices"},
            new String[]{"Clothing", "Men's, women's, children's clothing and shoes"},
            new String[]{"Home & Kitchen", "Home appliances, decoration, kitchen items"},
            new String[]{"Books & Magazines", "Books, magazines, educational software"},
            new String[]{"Health & Beauty", "Health products, cosmetics, personal care"},
            new String[]{"Sports & Travel", "Sports equipment, backpacks, luggage"},
            new String[]{"Automotive", "Spare parts, car accessories"},
            new String[]{"Food & Beverages", "Food items, drinks, groceries"},
            new String[]{"Industrial & Office", "Tools, industrial and office equipment"},
            new String[]{"Arts & Crafts", "Handicrafts, paintings, sculptures"}
        );
        
        int createdCount = 0;
        for (String[] categoryData : mainCategories) {
            if (!categoryRepository.existsByName(categoryData[0])) {
                Category category = new Category();
                category.setName(categoryData[0]);
                category.setDescription(categoryData[1]);
                category.setIsActive(true);
                
                categoryRepository.save(category);
                createdCount++;
                log.debug("Category {} created", categoryData[0]);
            }
        }
        
        log.info("✅ {} main categories created", createdCount);
    }

    private void initializeElectronicsSubCategories() {
        log.info("📱 Creating electronics subcategories...");
        
        Category electronics = categoryRepository.findByName("Electronics");
        if (electronics != null) {
            List<String[]> subCategories = Arrays.asList(
                new String[]{"Mobile Phones", "Various smartphones"},
                new String[]{"Laptops", "Portable computers"},
                new String[]{"Tablets", "Tablets and tablet computers"},
                new String[]{"Headphones", "Headphones and headsets"},
                new String[]{"Smart Watches", "Smart watches and fitness trackers"},
                new String[]{"Cameras", "Digital cameras and photography equipment"},
                new String[]{"Computers", "Personal computers and components"},
                new String[]{"Accessories", "Cables, chargers, cases"}
            );
            
            int createdCount = 0;
            for (String[] subCatData : subCategories) {
                if (!categoryRepository.existsByNameAndParent(subCatData[0], electronics)) {
                    Category subCategory = new Category();
                    subCategory.setName(subCatData[0]);
                    subCategory.setDescription(subCatData[1]);
                    subCategory.setIsActive(true);
                    subCategory.setParent(electronics);
                    
                    categoryRepository.save(subCategory);
                    createdCount++;
                    log.debug("Subcategory {} created", subCatData[0]);
                }
            }
            
            log.info("✅ {} electronics subcategories created", createdCount);
        } else {
            log.warn("⚠️ Electronics category not found");
        }
    }
}
