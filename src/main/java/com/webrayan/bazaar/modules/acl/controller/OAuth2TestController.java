package com.webrayan.bazaar.modules.acl.controller;

import com.webrayan.bazaar.modules.acl.entity.User;
import com.webrayan.bazaar.modules.acl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2TestController {

    private final UserRepository userRepository;

    /**
     * تست endpoint برای بررسی OAuth2 users
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getOAuth2Users() {
        List<User> oauth2Users = userRepository.findAll().stream()
                .filter(user -> user.getOauthProvider() != null)
                .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", oauth2Users.size());
        response.put("users", oauth2Users.stream().map(user -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getFirstName() + " " + user.getLastName());
            userInfo.put("provider", user.getOauthProvider());
            userInfo.put("verified", user.getVerified());
            userInfo.put("profilePicture", user.getProfilePicture());
            return userInfo;
        }).toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * تست endpoint برای بررسی configuration
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getOAuth2Config() {
        Map<String, Object> config = new HashMap<>();
        config.put("oauth2Enabled", true);
        config.put("supportedProviders", List.of("google"));
        config.put("loginUrl", "/oauth2/authorization/google");
        config.put("logoutUrl", "/oauth2/logout");
        
        return ResponseEntity.ok(config);
    }
}
