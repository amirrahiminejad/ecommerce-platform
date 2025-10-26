package com.webrayan.commerce.modules.ads.entity;

import com.webrayan.commerce.core.common.entity.BaseEntity;
import com.webrayan.commerce.modules.acl.entity.User;
import com.webrayan.commerce.modules.ads.enums.AdStatus;
import com.webrayan.commerce.modules.ads.enums.AdVisibility;
import com.webrayan.commerce.core.common.entity.Location;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
@Entity
@Table(name = "ads")
@Data
@EqualsAndHashCode(callSuper = false)
public class Ad extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;

    private Boolean negotiable = false;

    private Integer viewsCount = 0;

    private Boolean isFeatured = false;

    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status = AdStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdVisibility visibility = AdVisibility.PUBLIC;

    private String rejectionReason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private AdCategory category;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = true)
    private Location location;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<AdImage> images;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<AdAttributeValue> attributes;

    @OneToMany(mappedBy = "ad")
    private List<AdTag> tags;

}
