package com.webrayan.store.modules.ads.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ad_attribute_values")
public class AdAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false)
    private AdCategoryAttribute attribute;

    @Column(columnDefinition = "TEXT")
    private String value;

}
