package com.webrayan.commerce.core.common.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "locations")
@Data
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String city;

    private String region;

    private Double latitude;

    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country; // ارتباط با کشور

}
