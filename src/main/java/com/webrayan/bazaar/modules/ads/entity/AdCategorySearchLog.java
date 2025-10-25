package com.webrayan.bazaar.modules.ads.entity;

import com.webrayan.bazaar.modules.acl.entity.User;
import com.webrayan.bazaar.core.common.entity.Location;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "ad_category_seach_logs")
@Data
public class AdCategorySearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String query;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private AdCategory category;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
}
