package com.webrayan.commerce.modules.acl.service;

import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserSocialMediaTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

  // @Test
    public void testUpdateSocialMediaLinks() {
        // ایجاد کاربر تست
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        // به‌روزرسانی لینک‌های شبکه‌های اجتماعی
        String linkedinUrl = "https://linkedin.com/in/testuser";
        String facebookUrl = "https://facebook.com/testuser";
        String instagramUrl = "https://instagram.com/testuser";
        String whatsappUrl = "+989123456789";
        String telegramUrl = "@testuser";

        User updatedUser = userService.updateSocialMediaLinks(
            user.getId(), 
            linkedinUrl, 
            facebookUrl, 
            instagramUrl,
            whatsappUrl,
            telegramUrl
        );

        // بررسی نتایج
        assertNotNull(updatedUser);
        assertEquals(linkedinUrl, updatedUser.getLinkdin());
        assertEquals(facebookUrl, updatedUser.getFacebook());
        assertEquals(instagramUrl, updatedUser.getInstagram());
        assertEquals("https://wa.me/989123456789", updatedUser.getWhatsapp());
        assertEquals("https://t.me/testuser", updatedUser.getTelegram());
    }

  //  @Test
    public void testCleanUrlWithoutProtocol() {
        // ایجاد کاربر تست
        User user = new User();
        user.setUsername("testuser2");
        user.setEmail("test2@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        // تست با URL بدون پروتکل
        String linkedinUrl = "linkedin.com/in/testuser";
        
//        User updatedUser = userService.updateSocialMediaLinks(
//            user.getId(),
//            linkedinUrl,
//            null,
//            null
//        );

        // بررسی که https:// اضافه شده است
//        assertEquals("https://linkedin.com/in/testuser", updatedUser.getLinkdin());
    }

  //  @Test
    public void testEmptyAndNullUrls() {
        // ایجاد کاربر تست
        User user = new User();
        user.setUsername("testuser3");
        user.setEmail("test3@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        // تست با URL های خالی و null
        User updatedUser = userService.updateSocialMediaLinks(
            user.getId(), 
            "", 
            "   ", 
            null,
            "",
            null
        );

        // بررسی که همه null شده‌اند
        assertNull(updatedUser.getLinkdin());
        assertNull(updatedUser.getFacebook());
        assertNull(updatedUser.getInstagram());
        assertNull(updatedUser.getWhatsapp());
        assertNull(updatedUser.getTelegram());
    }

  //  @Test
    public void testWhatsAppFormatting() {
        // ایجاد کاربر تست
        User user = new User();
        user.setUsername("testuser4");
        user.setEmail("test4@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        // تست فرمت‌های مختلف WhatsApp
        User updatedUser1 = userService.updateSocialMediaLinks(
            user.getId(), null, null, null, "+989123456789", null
        );
        assertEquals("https://wa.me/989123456789", updatedUser1.getWhatsapp());

        User updatedUser2 = userService.updateSocialMediaLinks(
            user.getId(), null, null, null, "09123456789", null
        );
        assertEquals("https://wa.me/+09123456789", updatedUser2.getWhatsapp());
    }

//    @Test
    public void testTelegramFormatting() {
        // ایجاد کاربر تست
        User user = new User();
        user.setUsername("testuser5");
        user.setEmail("test5@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userRepository.save(user);

        // تست فرمت‌های مختلف Telegram
        User updatedUser1 = userService.updateSocialMediaLinks(
            user.getId(), null, null, null, null, "@testuser"
        );
        assertEquals("https://t.me/testuser", updatedUser1.getTelegram());

        User updatedUser2 = userService.updateSocialMediaLinks(
            user.getId(), null, null, null, null, "testuser"
        );
        assertEquals("https://t.me/testuser", updatedUser2.getTelegram());
    }
}
