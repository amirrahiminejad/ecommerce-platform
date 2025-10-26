package com.webrayan.commerce.modules.ads.entity;


import com.webrayan.commerce.modules.acl.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "ad_messages")
@Data
public class AdMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender;
    
    // فیلدهای اضافی برای کاربران غیر احراز هویت شده
    @Column(name = "sender_name", length = 100)
    private String senderName;
    
    @Column(name = "sender_email", length = 255)
    private String senderEmail;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    private Boolean isRead = false;

}
