package dev.kush.supportchatbot.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuAndUserId(String sku, String userId);

    @Modifying
    @Query("update Product p set p.quantity = p.quantity - :quantity where p.id = :id")
    void deductStockById(Long id, int quantity);
}