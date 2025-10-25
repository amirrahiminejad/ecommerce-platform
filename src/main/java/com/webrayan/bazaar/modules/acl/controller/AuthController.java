package com.webrayan.bazaar.modules.acl.controller;

import com.webrayan.bazaar.core.common.controller.BaseController;
import com.webrayan.bazaar.modules.acl.dto.LoginUserDto;
import com.webrayan.bazaar.modules.acl.dto.SignupUserDto;
import com.webrayan.bazaar.modules.acl.entity.User;
import com.webrayan.bazaar.modules.acl.entity.Role;
import com.webrayan.bazaar.modules.acl.repository.UserRepository;
import com.webrayan.bazaar.modules.acl.repository.RoleRepository;
import com.webrayan.bazaar.core.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Authentication", description = "API های مربوط به احراز هویت و مدیریت جلسه کاربر")
@RequestMapping("/api/auth")
@RestController
public class AuthController extends BaseController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    @Qualifier("customUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(
        summary = "Test endpoint", 
        description = "تست اتصال به سرویس احراز هویت"
    )
    @ApiResponse(
        responseCode = "200", 
        description = "اتصال موفق",
        content = @Content(
            mediaType = "text/plain",
            examples = @ExampleObject(value = "Ok")
        )
    )
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Ok");
    }

    @Operation(
        summary = "ورود کاربر", 
        description = "احراز هویت کاربر و دریافت JWT token برای دسترسی به API های محافظت شده"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "ورود موفق",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "success": true
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "نام کاربری یا رمز عبور اشتباه",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "message": "Invalid username or password",
                        "success": false
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Parameter(
            description = "اطلاعات ورود کاربر",
            required = true,
            schema = @Schema(implementation = LoginUserDto.class)
        )
        @Valid @RequestBody LoginUserDto login) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails);
            response.put("token", token);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            response.put("message", "Invalid username or password");
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }
    }

    @Operation(
        summary = "ثبت نام کاربر جدید", 
        description = "ایجاد حساب کاربری جدید در سیستم"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "ثبت نام موفق",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": true,
                        "message": "User registered successfully"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "ایمیل قبلاً ثبت شده یا خطا در اطلاعات",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                        "success": false,
                        "message": "Email already registered"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
        @Parameter(
            description = "اطلاعات ثبت نام کاربر",
            required = true,
            schema = @Schema(implementation = SignupUserDto.class)
        )
        @Valid @RequestBody SignupUserDto signupUser) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByEmail(signupUser.getEmail()).isPresent()) {
            response.put("success", false);
            response.put("message", "Email already registered");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            // پیدا کردن نقش CUSTOMER برای کاربران عادی
            Role customerRole = roleRepository.findByRoleName(Role.RoleName.CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("نقش CUSTOMER پیدا نشد"));

            User user = new User();
            user.setFirstName(signupUser.getFirstName());
            user.setLastName(signupUser.getLastName());
            user.setUsername(signupUser.getUsername());
            user.setEmail(signupUser.getEmail());
            user.setPhoneNumber(signupUser.getPhone());
            user.setPassword(passwordEncoder.encode(signupUser.getPassword()));
            user.setStatus(User.UserStatus.ACTIVE);
            user.setRoles(Set.of(customerRole));
            userRepository.save(user);
            response.put("success", true);
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/signup-form")
    public ResponseEntity<?> signupForm(
        @RequestParam String firstName,
        @RequestParam String lastName,
        @RequestParam String username,
        @RequestParam String email,
        @RequestParam(required = false) String phone,
        @RequestParam String password) {
        
        Map<String, Object> response = new HashMap<>();

        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            response.put("success", false);
            response.put("message", "این ایمیل قبلاً ثبت شده است");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            response.put("success", false);
            response.put("message", "این نام کاربری قبلاً ثبت شده است");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // پیدا کردن نقش CUSTOMER برای کاربران عادی
            Role customerRole = roleRepository.findByRoleName(Role.RoleName.CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("نقش CUSTOMER پیدا نشد"));

            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setEmail(email);
            user.setPhoneNumber(phone);
            user.setPassword(passwordEncoder.encode(password));
            user.setStatus(User.UserStatus.ACTIVE);
            user.setRoles(Set.of(customerRole));
            User savedUser = userRepository.save(user);
            
            // Auto login after successful registration
            try {
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
                );
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String token = jwtTokenProvider.generateToken(userDetails);
                
                response.put("success", true);
                response.put("message", "ثبت نام با موفقیت انجام شد");
                response.put("token", token);
                response.put("redirectUrl", "/profile");
                response.put("user", Map.of(
                    "id", savedUser.getId(),
                    "firstName", savedUser.getFirstName(),
                    "lastName", savedUser.getLastName(),
                    "email", savedUser.getEmail(),
                    "username", savedUser.getUsername()
                ));
                return ResponseEntity.ok(response);
            } catch (AuthenticationException e) {
                // If auto login fails, still return success for registration
                response.put("success", true);
                response.put("message", "ثبت نام با موفقیت انجام شد. لطفاً وارد شوید");
                response.put("redirectUrl", "/auth/login");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "خطا در ثبت نام: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        // شبیه‌سازی ارسال توکن بازیابی رمز عبور (در عمل باید ایمیل ارسال شود)
        String resetToken = java.util.UUID.randomUUID().toString();
        // این توکن را باید در دیتابیس ذخیره و اعتبارسنجی کنید (اینجا فقط نمایش داده می‌شود)
        return ResponseEntity.ok("Reset token: " + resetToken);
    }
}