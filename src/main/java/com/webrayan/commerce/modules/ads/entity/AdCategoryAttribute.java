package com.webrayan.commerce.modules.ads.entity;

import com.webrayan.commerce.modules.ads.enums.AttributeType;
import jakarta.persistence.*;

@Entity
@Table(name = "ad_category_attributes")
public class AdCategoryAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private AdCategory adCategory;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributeType type;

}
