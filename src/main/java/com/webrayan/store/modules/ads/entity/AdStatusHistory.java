package com.webrayan.store.modules.ads.entity;

import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.ads.enums.AdStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "ad_status_history")
@Data
public class AdStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdStatus status;

    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy; // کاربری که وضعیت را تغییر داده

    private String reason; // دلیل تغییر وضعیت

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date changedAt = new Date();

}
