package com.webrayan.store.modules.acl.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@Entity
@Table(name = "acl_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private RoleName roleName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "acl_role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;

    public enum RoleName {
        SYSTEM_ADMIN("مدیر سیستم"),
        EMPLOYEE("کارمند"),
        CUSTOMER("مشتری"),
        AFFILIATE("بازاریاب");

        private final String persianName;

        RoleName(String persianName) {
            this.persianName = persianName;
        }

        public String getPersianName() {
            return persianName;
        }
    }


}