package dev.kush.supportchatbot.product;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
class EmbeddingService {

    private final VectorStore vectorStore;

    public EmbeddingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    private void create(String text, String userId) {
        Document document = new Document(text, Map.of("userId", userId));
        vectorStore.add(List.of(document));
    }

    @Async
    void productEmbedding(ProductCreateRequest productCreateRequest, String userId) {
        String text = """
                product name: %s,
                product description: %s,
                product price: %s,
                product sku: %s
                """.formatted(productCreateRequest.name(), productCreateRequest.description(),
                productCreateRequest.price(), productCreateRequest.sku());
        create(text, userId);
    }
}
