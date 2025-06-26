package dev.kush.supportchatbot.order;

import dev.kush.supportchatbot.product.Product;
import dev.kush.supportchatbot.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public boolean create(OrderCreateRequest orderCreateRequest, String userId) {
        try {
            Product product = productRepository.findBySkuAndUserId(orderCreateRequest.productSku(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found for SKU: " + orderCreateRequest.productSku()));

            if (product.getQuantity() < orderCreateRequest.quantity()) {
                throw new IllegalArgumentException("Insufficient product quantity for SKU: " + orderCreateRequest.productSku());
            }

            Order order = new Order();
            order.setProductId(product.getId());
            order.setQuantity(orderCreateRequest.quantity());
            order.setUserId(userId);

            orderRepository.save(order);
            productRepository.deductStockById(product.getId(), orderCreateRequest.quantity());
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes
            return false; // In a real application, you might want to log the exception or handle it differently
        }
    }
}
