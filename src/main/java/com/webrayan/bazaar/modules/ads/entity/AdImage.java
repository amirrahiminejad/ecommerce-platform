package com.webrayan.bazaar.modules.ads.entity;

import com.webrayan.bazaar.core.common.entity.Image;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ad_images")
@Data
public class AdImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

}