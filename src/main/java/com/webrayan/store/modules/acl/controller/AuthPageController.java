package com.webrayan.store.modules.acl.controller;

import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.acl.entity.Role;
import com.webrayan.store.modules.acl.repository.UserRepository;
import com.webrayan.store.modules.acl.repository.RoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthPageController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage(Model model, 
                               @RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               java.security.Principal principal) {
        
        // If user is already logged in, redirect to profile
        if (principal != null) {
            return "redirect:/profile";
        }
        
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "You have been successfully logged out.");
        }
        
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        return "auth/register";
    }

    /**
     * پردازش ثبت نام از طریق فرم HTML
     */
    @PostMapping("/register")
    public String processRegister(@RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Password validation
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match");
                return "redirect:/auth/register";
            }

            // Check user existence
            if (userRepository.existsByUsername(username)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username is already taken");
                return "redirect:/auth/register";
            }

            if (userRepository.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email is already registered");
                return "redirect:/auth/register";
            }

            // Find CUSTOMER role for regular users
            Role customerRole = roleRepository.findByRoleName(Role.RoleName.CUSTOMER).get();

            // ایجاد کاربر جدید
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(User.UserStatus.ACTIVE);
            user.setRoles(Set.of(customerRole));

            userRepository.save(user);

            // ورود خودکار کاربر پس از ثبت نام
//            UsernamePasswordAuthenticationToken authToken =
//                new UsernamePasswordAuthenticationToken(username, password);
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            Authentication authentication = authenticationManager.authenticate(authToken);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
            redirectAttributes.addFlashAttribute("successMessage",
                "Registration completed successfully.");
            
            return "redirect:/auth/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Registration error: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }

}
