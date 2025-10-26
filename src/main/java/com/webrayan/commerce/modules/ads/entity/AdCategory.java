package com.webrayan.commerce.modules.ads.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ad_categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdCategory {

    public AdCategory(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private AdCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<AdCategory> subcategories = new ArrayList<>();


}