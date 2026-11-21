package com.tecsup.minishop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
}