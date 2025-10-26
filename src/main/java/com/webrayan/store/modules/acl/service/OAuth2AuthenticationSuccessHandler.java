package com.webrayan.store.modules.acl.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        
        // ذخیره اطلاعات کاربر در session
        HttpSession session = request.getSession();
        session.setAttribute("userId", oAuth2User.getUserId());
        session.setAttribute("userEmail", oAuth2User.getEmail());
        session.setAttribute("userName", oAuth2User.getUser().getFirstName() + " " + oAuth2User.getUser().getLastName());
        
        // لاگ موفق ورود
        System.out.println("OAuth2 Login successful for user: " + oAuth2User.getEmail());
        
        // ریدایرکت به صفحه اصلی یا پروفایل
        String redirectUrl = getRedirectUrl(request);
        response.sendRedirect(redirectUrl);
    }

    private String getRedirectUrl(HttpServletRequest request) {
        // اگر کاربر از صفحه خاصی آمده، به همان صفحه برگردد
        String savedRequest = (String) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedRequest != null) {
            request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
            return savedRequest;
        }
        
        // در غیر این صورت به پروفایل کاربر برود
        return "/profile";
    }
}
