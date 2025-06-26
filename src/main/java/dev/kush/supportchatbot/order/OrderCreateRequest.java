package dev.kush.supportchatbot.order;

public record OrderCreateRequest(
        int quantity, String productSku
) {
}
