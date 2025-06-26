package dev.kush.supportchatbot.product;

public record ProductCreateRequest(
        String name,
        String sku,
        String description,
        Double price,
        int quantity
) {
}
