package com.tecsup.minishop.repository;

import com.tecsup.minishop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryIT {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe guardar un producto y asignarle ID automáticamente")
    void shouldSaveProductAndAssignId() {
        // ARRANGE
        Product product = Product.builder().name("Laptop Lenovo").price(2500.00).stock(10).build();
        // ACT: forzar escritura y limpiar caché para comprobar la BD real.
        Product saved = productRepository.saveAndFlush(product);
        entityManager.clear();
        // ASSERT
        assertThat(saved.getId()).isNotNull();
        Product persisted = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(persisted.getName()).isEqualTo("Laptop Lenovo");
        assertThat(persisted.getPrice()).isEqualTo(2500.00);
        assertThat(persisted.getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("Debe encontrar un producto por ID existente")
    void shouldFindProductById() {
        Product product = productRepository.saveAndFlush(
                Product.builder().name("Mouse Logitech").price(85.00).stock(50).build());
        entityManager.clear();

        var found = productRepository.findById(product.getId());

        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getName()).isEqualTo("Mouse Logitech");
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el ID no existe")
    void shouldReturnEmptyWhenIdNotFound() {
        assertThat(productRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("Debe buscar productos por nombre ignorando mayúsculas")
    void shouldFindProductsByNameIgnoringCase() {
        productRepository.save(Product.builder().name("Teclado Mecánico").price(150.00).stock(20).build());
        productRepository.save(Product.builder().name("Teclado Membrana").price(45.00).stock(30).build());
        productRepository.save(Product.builder().name("Monitor Dell").price(800.00).stock(5).build());
        productRepository.flush();
        entityManager.clear();

        var result = productRepository.findByNameContainingIgnoreCase("teclado");

        assertThat(result).hasSize(2).extracting(Product::getName)
                .containsExactlyInAnyOrder("Teclado Mecánico", "Teclado Membrana");
    }
}
