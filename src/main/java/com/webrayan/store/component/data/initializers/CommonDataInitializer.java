package com.webrayan.store.component.data.initializers;

import com.webrayan.store.core.common.entity.Country;
import com.webrayan.store.core.common.entity.Location;
import com.webrayan.store.core.common.entity.Setting;
import com.webrayan.store.core.common.entity.Tag;
import com.webrayan.store.core.common.repository.CountryRepository;
import com.webrayan.store.core.common.repository.LocationRepository;
import com.webrayan.store.core.common.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonDataInitializer {

    private final CountryRepository countryRepository;
    private final LocationRepository locationRepository;
    private final SettingRepository settingRepository;

    @Transactional
    public void initialize() {
        log.info("📍 Starting common data initialization...");
        
        initializeCountries();
        initializeLocations();
        initializeSettings();
        initializeTags();
        
        log.info("✅ Common data initialized successfully");
    }

    private void initializeCountries() {
        log.info("🌍 Creating default countries...");
        
        List<String[]> countryData = Arrays.asList(
            new String[]{"Australia", "AU"},
            new String[]{"Germany", "DE"},
            new String[]{"Turkey", "TR"},
            new String[]{"United States", "US"},
            new String[]{"Canada", "CA"},
            new String[]{"United Kingdom", "GB"},
            new String[]{"France", "FR"},
            new String[]{"Italy", "IT"}
        );
        
        int createdCount = 0;
        for (String[] data : countryData) {
            if (!countryRepository.existsByCode(data[1])) {
                Country country = new Country(data[0], data[1]);
                countryRepository.save(country);
                createdCount++;
                log.debug("Country {} created", data[0]);
            }
        }
        
        log.info("✅ {} countries created", createdCount);
    }

    private void initializeLocations() {
        log.info("🏙️ Creating Australian cities...");
        
        // Find Australia country first
        Country australia = countryRepository.findByCode("AU");
        if (australia == null) {
            log.error("Australia country not found. Make sure countries are initialized first.");
            return;
        }
        
        // Australian cities with coordinates
        List<String[]> australianCities = Arrays.asList(
            // Major Cities
            new String[]{"Sydney", "New South Wales", "-33.8688", "151.2093"},
            new String[]{"Melbourne", "Victoria", "-37.8136", "144.9631"},
            new String[]{"Brisbane", "Queensland", "-27.4698", "153.0251"},
            new String[]{"Perth", "Western Australia", "-31.9505", "115.8605"},
            new String[]{"Adelaide", "South Australia", "-34.9285", "138.6007"},
            new String[]{"Canberra", "Australian Capital Territory", "-35.2809", "149.1300"},
            new String[]{"Darwin", "Northern Territory", "-12.4634", "130.8456"},
            new String[]{"Hobart", "Tasmania", "-42.8821", "147.3272"},
            
            // Other Major Cities
            new String[]{"Gold Coast", "Queensland", "-28.0167", "153.4000"},
            new String[]{"Newcastle", "New South Wales", "-32.9283", "151.7817"},
            new String[]{"Wollongong", "New South Wales", "-34.4250", "150.8931"},
            new String[]{"Geelong", "Victoria", "-38.1499", "144.3617"},
            new String[]{"Townsville", "Queensland", "-19.2590", "146.8169"},
            new String[]{"Cairns", "Queensland", "-16.9186", "145.7781"},
            new String[]{"Toowoomba", "Queensland", "-27.5598", "151.9507"},
            new String[]{"Ballarat", "Victoria", "-37.5622", "143.8503"},
            new String[]{"Bendigo", "Victoria", "-36.7570", "144.2794"},
            new String[]{"Albury", "New South Wales", "-36.0737", "146.9135"},
            new String[]{"Launceston", "Tasmania", "-41.4332", "147.1441"},
            new String[]{"Mackay", "Queensland", "-21.1550", "149.1868"},
            
            // Regional Centers
            new String[]{"Rockhampton", "Queensland", "-23.3781", "150.5136"},
            new String[]{"Bunbury", "Western Australia", "-33.3266", "115.6379"},
            new String[]{"Bundaberg", "Queensland", "-24.8661", "152.3489"},
            new String[]{"Wagga Wagga", "New South Wales", "-35.1082", "147.3598"},
            new String[]{"Hervey Bay", "Queensland", "-25.2988", "152.8544"},
            new String[]{"Mildura", "Victoria", "-34.2085", "142.1405"},
            new String[]{"Shepparton", "Victoria", "-36.3820", "145.3980"},
            new String[]{"Orange", "New South Wales", "-33.2838", "149.0988"},
            new String[]{"Dubbo", "New South Wales", "-32.2431", "148.6017"},
            new String[]{"Geraldton", "Western Australia", "-28.7774", "114.6139"}
        );
        
        int createdCount = 0;
        for (String[] cityData : australianCities) {
            String cityName = cityData[0];
            String region = cityData[1];
            Double latitude = Double.parseDouble(cityData[2]);
            Double longitude = Double.parseDouble(cityData[3]);
            
            // Check if location already exists
            if (!locationRepository.existsByCityAndCountry(cityName, australia)) {
                Location location = new Location();
                location.setCity(cityName);
                location.setRegion(region);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                location.setCountry(australia);
                
                locationRepository.save(location);
                createdCount++;
                log.debug("Location {} ({}) created", cityName, region);
            }
        }
        
        log.info("✅ {} Australian cities created", createdCount);
    }

    private void initializeSettings() {
        log.info("⚙️ Creating default settings...");
        
        List<String[]> settingData = Arrays.asList(
            new String[]{"site_name", "فروشگاه اینترنتی"},
            new String[]{"site_title", "فروشگاه آنلاین"},
            new String[]{"site_description", "فروشگاه اینترنتی با محصولات متنوع و کیفیت بالا"},
            new String[]{"default_currency", "تومان"},
            new String[]{"default_language", "fa"},
            new String[]{"admin_email", "admin@online-store.com"},
            new String[]{"support_phone", "+98-21-12345678"},
            new String[]{"max_upload_size", "10485760"},
            new String[]{"items_per_page", "20"},
            new String[]{"maintenance_mode", "false"}
        );
        
        int createdCount = 0;
        for (String[] data : settingData) {
            if (!settingRepository.existsByKey(data[0])) {
                Setting setting = new Setting();
                setting.setKey(data[0]);
                setting.setValue(data[1]);
                setting.setUser(null); // System-wide setting
                settingRepository.save(setting);
                createdCount++;
                log.debug("Setting {} created", data[0]);
            }
        }
        
        log.info("✅ {} settings created", createdCount);
    }

    private void initializeTags() {
        log.info("🏷️ Creating default tags...");
        
        List<String> tagNames = Arrays.asList(
            "جدید", "پیشنهاد ویژه", "فروش ویژه", "محبوب", "پرفروش",
            "ارگانیک", "دست‌ساز", "صادراتی", "وارداتی", "اصل",
            "تخفیف‌دار", "رایگان", "سریع", "کیفیت بالا", "محدود"
        );
        
        int createdCount = 0;
        for (String tagName : tagNames) {
            // Note: Tag creation logic would need the Tag repository and entity
            // This is a placeholder for when Tag entity and repository are available
            log.debug("Tag {} would be created", tagName);
            createdCount++;
        }
        
        log.info("✅ {} tags prepared for creation", createdCount);
    }


}
