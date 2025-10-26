package com.webrayan.store.component.data.initializers;

import com.webrayan.store.modules.ads.entity.Ad;
import com.webrayan.store.modules.ads.entity.AdCategory;
import com.webrayan.store.modules.ads.repository.AdCategoryRepository;
import com.webrayan.store.modules.ads.repository.AdRepository;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.repository.UserRepository;
import com.webrayan.store.core.common.entity.Location;
import com.webrayan.store.core.common.repository.LocationRepository;
import com.webrayan.store.modules.ads.enums.AdStatus;
import com.webrayan.store.modules.ads.enums.AdVisibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Calendar;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdsDataInitializer {

    private final AdCategoryRepository adCategoryRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public void initialize() {
        log.info("📢 Starting ads data initialization...");
        
        initializeAdCategories();
       // initializeSampleAds();
        
        log.info("✅ Ads data initialized successfully");
    }

    private void initializeAdCategories() {
        log.info("📋 Creating ad categories...");
        
        List<String> adCategories = Arrays.asList(
            "Real Estate", "Automotive", "Home & Garden", "Services", "Jobs",
            "Business & Industrial", "Personal", "Sports & Recreation", "Pets", "Miscellaneous"
        );
        
        int createdCount = 0;
        for (String categoryName : adCategories) {
            if (!adCategoryRepository.existsByName(categoryName)) {
                AdCategory adCategory = new AdCategory();
                adCategory.setName(categoryName);
                
                adCategoryRepository.save(adCategory);
                createdCount++;
                log.debug("Ad category {} created", categoryName);
            }
        }
        
        log.info("✅ {} ad categories created", createdCount);
    }

    private void initializeSampleAds() {
        log.info("🏷️ Creating sample ads...");
        
        // Check if ads already exist in system
        if (adRepository.count() > 0) {
            log.info("Ads already exist in system, skipping sample ads creation");
            return;
        }

        // Get first user (admin or sample user)
        User user = userRepository.findAll().stream().findFirst().orElse(null);
        if (user == null) {
            log.warn("No user exists in system, cannot create ads");
            return;
        }

        // Get categories and locations
        List<AdCategory> categories = adCategoryRepository.findAll();
        List<Location> locations = locationRepository.findAll();
        
        if (categories.isEmpty()) {
            log.warn("No categories available");
            return;
        }

        Random random = new Random();
        Calendar cal = Calendar.getInstance();
        
        // Base sample ads
        String[][] baseSampleAds = {
            {"Apartment", "{} sqm apartment, {} bedrooms, floor {}, with elevator, parking and storage", "2500000000", "Real Estate"},
            {"Pride Car", "Pride {} color {}, {} km mileage, single owner", "320000000", "Automotive"},
            {"Refrigerator Freezer", "{} {} cubic feet refrigerator freezer, new condition, valid warranty", "25000000", "Home & Garden"},
            {"Cleaning Services", "Complete cleaning of {} and staircase, experienced staff with modern equipment", "500000", "Services"},
            {"Programmer Position", "Hiring {} programmer with minimum {} years experience, negotiable salary", "0", "Jobs"},
            {"Industrial Equipment", "Industrial {} machine for sale, {} condition, reasonable price", "150000000", "Business & Industrial"},
            {"Private Tutoring", "Private {} tutoring for all grades, experienced teacher", "200000", "Personal"},
            {"Bicycle", "{} bicycle {} inch, {} brand, excellent condition", "8000000", "Sports & Recreation"},
            {"Pet", "{} breed {} {} months old, vaccinated, with papers", "15000000", "Pets"},
            {"Books", "Collection of {} field books, {} condition", "1500000", "Miscellaneous"}
        };
        
        // Various words and values for diversity
        String[] sizes = {"75", "85", "95", "105", "120", "140", "160", "180", "200", "250"};
        String[] rooms = {"1", "2", "2", "3", "3", "4"};
        String[] floors = {"Ground", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th"};
        String[] colors = {"White", "Black", "Silver", "Blue", "Red", "Gray"};
        String[] carModels = {"132", "131", "111", "Pars", "Samand", "Rana"};
        String[] distances = {"25000", "45000", "65000", "85000", "120000", "150000"};
        String[] brands = {"Samsung", "LG", "Bosch", "Snowa", "Emersun", "Electrolux"};
        String[] appliances = {"20", "22", "24", "26", "28", "30"};
        String[] services = {"Residential", "Office", "Commercial", "Industrial"};
        String[] languages = {"Java", "Python", "C#", "JavaScript", "PHP", "Go"};
        String[] experience = {"1", "2", "3", "4", "5"};
        String[] machines = {"Lathe", "Mill", "Drill", "Welding", "Cutting"};
        String[] conditions = {"Excellent", "Good", "New", "Used"};
        String[] subjects = {"Mathematics", "Physics", "Chemistry", "English", "Arabic"};
        String[] bikeTypes = {"Mountain", "City", "Sport", "Kids"};
        String[] bikeSizes = {"20", "24", "26", "27", "28"};
        String[] bikeSpecs = {"Giant", "Trek", "Specialized", "Cannondale"};
        String[] animals = {"Dog", "Cat", "Rabbit", "Parrot"};
        String[] animalBreeds = {"Husky", "German Shepherd", "Persian", "Siamese", "Lop", "Cockatoo"};
        String[] ages = {"2", "6", "8", "12", "18", "24"};
        String[] fields = {"Computer Science", "Engineering", "Medicine", "Law", "Economics"};
        String[] bookConditions = {"New", "Like New", "Good", "Acceptable"};

        int createdCount = 0;
        // Create 100 diverse ads
        for (int i = 0; i < 100; i++) {
            String[] baseAd = baseSampleAds[i % baseSampleAds.length];
            try {
                String title = baseAd[0];
                String description = baseAd[1];
                String price = baseAd[2];
                String categoryName = baseAd[3];
                
                // Create diverse titles and descriptions
                switch (i % baseSampleAds.length) {
                    case 0: // Apartment
                        title = sizes[random.nextInt(sizes.length)] + " sqm Apartment in " + 
                               (locations.isEmpty() ? "Tehran" : locations.get(random.nextInt(locations.size())).getCity());
                        description = String.format(description, sizes[random.nextInt(sizes.length)], 
                                                   rooms[random.nextInt(rooms.length)], 
                                                   floors[random.nextInt(floors.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(1000000000));
                        break;
                    case 1: // Car
                        title = carModels[random.nextInt(carModels.length)] + " Car Model " + (2010 + random.nextInt(10));
                        description = String.format(description, carModels[random.nextInt(carModels.length)],
                                                   colors[random.nextInt(colors.length)],
                                                   distances[random.nextInt(distances.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(500000000));
                        break;
                    case 2: // Refrigerator
                        title = brands[random.nextInt(brands.length)] + " Refrigerator Freezer";
                        description = String.format(description, brands[random.nextInt(brands.length)],
                                                   appliances[random.nextInt(appliances.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(50000000));
                        break;
                    case 3: // Cleaning Services
                        title = services[random.nextInt(services.length)] + " Cleaning Services";
                        description = String.format(description, services[random.nextInt(services.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(1000000));
                        break;
                    case 4: // Job Hiring
                        title = languages[random.nextInt(languages.length)] + " Programmer Position";
                        description = String.format(description, languages[random.nextInt(languages.length)],
                                                   experience[random.nextInt(experience.length)]);
                        break;
                    case 5: // Industrial Equipment
                        title = machines[random.nextInt(machines.length)] + " Industrial Equipment";
                        description = String.format(description, machines[random.nextInt(machines.length)],
                                                   conditions[random.nextInt(conditions.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(200000000));
                        break;
                    case 6: // Private Tutoring
                        title = subjects[random.nextInt(subjects.length)] + " Private Tutoring";
                        description = String.format(description, subjects[random.nextInt(subjects.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(500000));
                        break;
                    case 7: // Bicycle
                        title = bikeTypes[random.nextInt(bikeTypes.length)] + " Bicycle";
                        description = String.format(description, bikeTypes[random.nextInt(bikeTypes.length)],
                                                   bikeSizes[random.nextInt(bikeSizes.length)],
                                                   bikeSpecs[random.nextInt(bikeSpecs.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(15000000));
                        break;
                    case 8: // Pet
                        title = animalBreeds[random.nextInt(animalBreeds.length)] + " " + animals[random.nextInt(animals.length)];
                        description = String.format(description, animals[random.nextInt(animals.length)],
                                                   animalBreeds[random.nextInt(animalBreeds.length)],
                                                   ages[random.nextInt(ages.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(30000000));
                        break;
                    case 9: // Books
                        title = fields[random.nextInt(fields.length)] + " Books";
                        description = String.format(description, fields[random.nextInt(fields.length)],
                                                   bookConditions[random.nextInt(bookConditions.length)]);
                        price = String.valueOf(Long.parseLong(price) + random.nextInt(5000000));
                        break;
                }

                Ad ad = new Ad();
                ad.setTitle(title);
                ad.setDescription(description);
                ad.setPrice(Double.parseDouble(price));
                ad.setUser(user);
                ad.setStatus(AdStatus.APPROVED);
                ad.setVisibility(AdVisibility.PUBLIC);
                ad.setIsActive(true);
                ad.setIsFeatured(random.nextBoolean());
                ad.setViewsCount(random.nextInt(100));

                // Set category
                AdCategory category = categories.stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .orElse(categories.get(0));
                ad.setCategory(category);

                // Set location (optional)
                if (!locations.isEmpty()) {
                    ad.setLocation(locations.get(random.nextInt(locations.size())));
                }

                // Set expiration date (30 days from now)
                cal.add(Calendar.DAY_OF_MONTH, 30);
                ad.setExpirationDate(cal.getTime());
                cal.add(Calendar.DAY_OF_MONTH, -30); // Return to today's date

                adRepository.save(ad);
                createdCount++;
                
            } catch (Exception e) {
                log.error("Error creating ad: ", e);
            }
        }

        log.info("✅ {} sample ads created", createdCount);
    }
}
