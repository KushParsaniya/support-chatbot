package dev.kush.supportchatbot.agent.tools;

import dev.kush.supportchatbot.config.UserUtils;
import dev.kush.supportchatbot.order.OrderCreateRequest;
import dev.kush.supportchatbot.order.OrderService;
import org.springframework.ai.tool.annotation.Tool;

public class OrderTools {

    private final OrderService orderService;

    public OrderTools(OrderService orderService) {
        this.orderService = orderService;
    }

    @Tool(description = """
            Creates a new order. To do so, you must collect and confirm the following required fields from the user:
            
            - **sku**: The SKU (Stock Keeping Unit) of the product.
            - **quantity**: The quantity of the product to order.
            
            If either of these fields is missing or unclear, **ask the user for it explicitly** before proceeding.
            """)
    public String createOrder(String sku, int quantity) {
        if (sku == null || sku.isEmpty()) {
            return "Please provide a valid SKU for the product.";
        }
        if (quantity <= 0) {
            return "Please provide a valid quantity greater than zero.";
        }

        // Call the order service to create the order
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(quantity, sku);
        String userId = UserUtils.getUserId();

        if (userId == null || userId.isEmpty()) {
            return "User ID is not available. Please log in to create an order.";
        }
        boolean result = orderService.create(orderCreateRequest, userId);
        if (result) {
            return "Order created successfully for SKU: " + sku + " with quantity: " + quantity;
        } else {
            // here you can handle the case where order creation fails
            // like quantity not available, SKU not found, etc.
            return "Failed to create order. Please try again later.";
        }
    }
}
