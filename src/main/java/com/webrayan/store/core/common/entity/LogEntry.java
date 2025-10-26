package com.webrayan.store.core.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_entries")
@Data
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String service;
    private String operation;
    private String message;
    private String username;
    private LocalDateTime timestamp = LocalDateTime.now();
}
