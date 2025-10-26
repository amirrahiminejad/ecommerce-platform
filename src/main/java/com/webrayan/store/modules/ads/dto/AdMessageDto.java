package com.webrayan.store.modules.ads.dto;

import lombok.Data;

@Data
public class AdMessageDto {
    
    private Long adId;
    private String content;
    
    // Fields for non-authenticated users
    private String senderName;
    private String senderEmail;
}
