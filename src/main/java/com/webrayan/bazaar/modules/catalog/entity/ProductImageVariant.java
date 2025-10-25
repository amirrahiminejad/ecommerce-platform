package com.webrayan.bazaar.modules.catalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_image_variants")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_image_id", nullable = false)
    private ProductImage productImage;

    @Column(name = "size_name", nullable = false, length = 50)
    private String sizeName;

    @Column(name = "width", nullable = false)
    private Integer width;

    @Column(name = "height", nullable = false)
    private Integer height;

    @Column(name = "format", nullable = false, length = 10)
    private String format;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_reference", nullable = false, length = 500)
    private String fileReference;

    @Column(name = "url", length = 1000)
    private String url;
}
