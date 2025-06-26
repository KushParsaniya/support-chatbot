package dev.kush.supportchatbot.product;

import org.springframework.stereotype.Service;

@Service
class ProductService {

    private final ProductRepository productRepository;
    private final EmbeddingService embeddingService;

    public ProductService(ProductRepository productRepository, EmbeddingService embeddingService) {
        this.productRepository = productRepository;
        this.embeddingService = embeddingService;
    }

    void create(ProductCreateRequest productCreateRequest, String userId) {
        Product product = new Product();
        product.setName(productCreateRequest.name());
        product.setSku(productCreateRequest.sku());
        product.setDescription(productCreateRequest.description());
        product.setPrice(productCreateRequest.price());
        product.setQuantity(productCreateRequest.quantity());
        product.setUserId(userId);
        embeddingService.productEmbedding(productCreateRequest, userId);
        productRepository.save(product);
        // also add it to the vector database
    }


}
