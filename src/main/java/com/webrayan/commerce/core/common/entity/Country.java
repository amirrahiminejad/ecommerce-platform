package com.webrayan.commerce.core.common.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "countries")
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code; // کد کشور (مثلاً "IR" برای ایران)

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Location> cities; // ارتباط با شهرها

    // Constructors, Getters, and Setters
    public Country() {}

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }


}
