package com.webrayan.commerce.modules.acl.service;

import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.acl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        UserInfo userInfo = extractUserInfo(registrationId, attributes);
        
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user = updateExistingUser(user, userInfo);
        } else {
            user = createNewUser(userInfo);
        }
        
        return new CustomOAuth2User(oauth2User.getAuthorities(), attributes, "email", user);
    }

    private UserInfo extractUserInfo(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return UserInfo.builder()
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .picture((String) attributes.get("picture"))
                .providerId((String) attributes.get("sub"))
                .provider("google")
                .build();
        }
        
        throw new OAuth2AuthenticationException("نام ارائه‌دهنده OAuth2 پشتیبانی نمی‌شود: " + registrationId);
    }

    private User createNewUser(UserInfo userInfo) {
        User user = new User();
        user.setEmail(userInfo.getEmail());
        user.setFirstName(extractFirstName(userInfo.getName()));
        user.setLastName(extractLastName(userInfo.getName()));
        user.setPhoneNumber(""); // کاربر بعداً می‌تواند تکمیل کند
        user.setPassword(""); // OAuth2 users don't need password
        user.setVerified(true); // Gmail verified users
        user.setOauthProvider(userInfo.getProvider());
        user.setOauthProviderId(userInfo.getProviderId());
        user.setProfilePicture(userInfo.getPicture());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, UserInfo userInfo) {
        existingUser.setOauthProvider(userInfo.getProvider());
        existingUser.setOauthProviderId(userInfo.getProviderId());
        existingUser.setProfilePicture(userInfo.getPicture());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(existingUser);
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length > 1) {
            return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
        }
        return "";
    }

    // Inner class for user info
    private static class UserInfo {
        private String email;
        private String name;
        private String picture;
        private String providerId;
        private String provider;

        public static UserInfoBuilder builder() {
            return new UserInfoBuilder();
        }

        // Builder pattern
        public static class UserInfoBuilder {
            private String email;
            private String name;
            private String picture;
            private String providerId;
            private String provider;

            public UserInfoBuilder email(String email) {
                this.email = email;
                return this;
            }

            public UserInfoBuilder name(String name) {
                this.name = name;
                return this;
            }

            public UserInfoBuilder picture(String picture) {
                this.picture = picture;
                return this;
            }

            public UserInfoBuilder providerId(String providerId) {
                this.providerId = providerId;
                return this;
            }

            public UserInfoBuilder provider(String provider) {
                this.provider = provider;
                return this;
            }

            public UserInfo build() {
                UserInfo userInfo = new UserInfo();
                userInfo.email = this.email;
                userInfo.name = this.name;
                userInfo.picture = this.picture;
                userInfo.providerId = this.providerId;
                userInfo.provider = this.provider;
                return userInfo;
            }
        }

        // Getters
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getPicture() { return picture; }
        public String getProviderId() { return providerId; }
        public String getProvider() { return provider; }
    }
}
