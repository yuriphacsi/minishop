package com.tecsup.minishop.controller;

import com.tecsup.minishop.dto.ProductDTO;
import com.tecsup.minishop.model.Product;
import com.tecsup.minishop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Método auxiliar para convertir de Entity a DTO
    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO create(@RequestBody Product product) {
        return toDTO(productService.save(product));
    }

    @GetMapping
    public List<ProductDTO> findAll() {
        return productService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductDTO findByID(@PathVariable Long id) {
        return toDTO(productService.findById(id));
    }
}