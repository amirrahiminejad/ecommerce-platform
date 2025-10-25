package com.webrayan.bazaar.modules.sale.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartDto {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String selectedAttributes; // JSON format
}
