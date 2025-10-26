package com.webrayan.store.modules.ads.entity;
import com.webrayan.store.modules.acl.entity.User;
import com.webrayan.store.modules.ads.enums.ViolationReportStatus;
import jakarta.persistence.*;

import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "ad_violation_reports")
@Data
public class AdViolationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @ManyToOne
    @JoinColumn(name = "reported_ad_id")
    private Ad reportedAd;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Enumerated(EnumType.STRING)
    private ViolationReportStatus status = ViolationReportStatus.PENDING;

}
