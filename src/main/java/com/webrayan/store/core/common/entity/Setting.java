package com.webrayan.store.core.common.entity;



import com.webrayan.store.modules.acl.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "settings")
@Data
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String key;

    private String value;

    @ManyToOne
    private User user;

}
