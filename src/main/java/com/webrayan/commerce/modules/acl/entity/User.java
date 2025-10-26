package com.webrayan.commerce.modules.acl.entity;

import com.webrayan.commerce.core.common.entity.BaseEntity;
import com.webrayan.commerce.core.common.entity.Location;
import com.webrayan.commerce.core.common.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Table(name = "acl_users")
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    public User(Long id) {
        this.setId(id);
    }

    @Column(name = "user_uuid", unique = true, nullable = false)
    private UUID userUuid = UUID.randomUUID();

    @Column(nullable = false)
    private String username;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(length = 100)
    private String linkdin;

    @Column(length = 100)
    private String facebook;

    @Column(length = 100)
    private String instagram;

    @Column(length = 100)
    private String whatsapp;

    @Column(length = 100)
    private String telegram;

    @Column(length = 100)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "acl_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;


    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(name = "national_code", length = 20)
    private String nationalCode;

    // OAuth2 fields
    @Column(name = "oauth_provider", length = 50)
    private String oauthProvider;

    @Column(name = "oauth_provider_id")
    private String oauthProviderId;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "verified")
    private Boolean verified = false;

    public enum UserStatus {
        ACTIVE("فعال"),
        INACTIVE("غیرفعال"),
        SUSPENDED("تعلیق"),
        BANNED("مسدود شده"),
        DELETED("حذف شده"),
        PENDING_VERIFICATION("در انتظار تأیید");

        private final String persianName;

        UserStatus(String persianName) {
            this.persianName = persianName;
        }

        public String getPersianName() {
            return persianName;
        }
    }
}