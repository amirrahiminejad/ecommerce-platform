package com.webrayan.store.component.data.initializers;

import com.webrayan.store.modules.catalog.entity.Category;
import com.webrayan.store.modules.catalog.repository.CategoryRepository;
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
            new String[]{"کالاهای دیجیتال", "موبایل، لپ‌تاپ، تبلت و سایر تجهیزات الکترونیکی"},
            new String[]{"مد و پوشاک", "پوشاک مردانه، زنانه، بچه‌گانه و کفش"},
            new String[]{"خانه و آشپزخانه", "لوازم خانگی، دکوراسیون، وسایل آشپزخانه"},
            new String[]{"کتاب و مجله", "کتاب، مجله، نرم‌افزارهای آموزشی"},
            new String[]{"بهداشت و زیبایی", "محصولات بهداشتی، آرایشی، مراقبت شخصی"},
            new String[]{"ورزش و سفر", "تجهیزات ورزشی، کوله‌پشتی، چمدان"},
            new String[]{"خودرو و وسایل نقلیه", "قطعات یدکی، لوازم جانبی خودرو"},
            new String[]{"مواد غذایی و نوشیدنی", "مواد غذایی، نوشیدنی، مواد اولیه"},
            new String[]{"صنعتی و اداری", "ابزارآلات، تجهیزات صنعتی و اداری"},
            new String[]{"هنر و صنایع دستی", "صنایع‌دستی، نقاشی، مجسمه"}
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
        
        Category electronics = categoryRepository.findByName("کالاهای دیجیتال");
        if (electronics != null) {
            List<String[]> subCategories = Arrays.asList(
                new String[]{"گوشی موبایل", "انواع گوشی‌های هوشمند"},
                new String[]{"لپ‌تاپ", "رایانه‌های قابل حمل"},
                new String[]{"تبلت", "تبلت و رایانه‌های لوحی"},
                new String[]{"هدفون و هندزفری", "هدفون و هندزفری"},
                new String[]{"ساعت هوشمند", "ساعت‌های هوشمند و ردیاب تناسب اندام"},
                new String[]{"دوربین", "دوربین‌های دیجیتال و تجهیزات عکاسی"},
                new String[]{"کامپیوتر", "رایانه‌های شخصی و قطعات"},
                new String[]{"لوازم جانبی", "کابل، شارژر، کیف"}
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
            log.warn("⚠️ کالاهای دیجیتال category not found");
        }
    }
}
