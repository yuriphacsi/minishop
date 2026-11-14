package com.tecsup.minishop.service;

import com.tecsup.minishop.model.Product;
import com.tecsup.minishop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    private ProductService productService;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        productService = new ProductService(repository);
    }

    @Test
    void save_precioCero_lanzaExcepcion() {
        // Arrange
        Product product = Product.builder().name("Mouse").price(0.0).stock(5).build();
        // Act + Assert
        var error = assertThrows(IllegalArgumentException.class, () -> productService.save(product));
        assertEquals("El precio debe ser mayor a cero", error.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void save_stockNegativo_lanzaExcepcion() {
        Product product = Product.builder().name("Mouse").price(50.0).stock(-1).build();

        var error = assertThrows(IllegalArgumentException.class, () -> productService.save(product));
        assertEquals("El stock no puede ser negativo", error.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void save_productoValido_noLanzaExcepcion() {
        Product product = Product.builder().name("Mouse").price(50.0).stock(5).build();
        Product saved = Product.builder().id(1L).name("Mouse").price(50.0).stock(5).build();
        when(repository.save(product)).thenReturn(saved);

        Product result = assertDoesNotThrow(() -> productService.save(product));

        assertSame(saved, result);
        verify(repository).save(product);
    }
}
