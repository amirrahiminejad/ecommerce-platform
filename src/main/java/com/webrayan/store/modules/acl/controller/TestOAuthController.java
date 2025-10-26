package com.webrayan.store.modules.acl.controller;

import com.webrayan.store.core.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestOAuthController {

    private final AuthenticationUtil authenticationUtil;

    @GetMapping("/oauth2")
    public String oauth2Test(Model model) {
        model.addAttribute("isOAuth2User", authenticationUtil.isOAuth2User());
        model.addAttribute("isAuthenticated", authenticationUtil.isAuthenticated());
        
        authenticationUtil.getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
        });
        
        return "test/oauth2-test";
    }
}
