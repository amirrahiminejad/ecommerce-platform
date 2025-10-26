package com.webrayan.commerce.modules.ads.entity;

import com.webrayan.commerce.core.common.entity.Tag;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ad_tags")
@Data
public class AdTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}