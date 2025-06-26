package dev.kush.supportchatbot.agent.tools;

import dev.kush.supportchatbot.config.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

public class ProductTools {

    Logger log = LoggerFactory.getLogger(ProductTools.class);
    private final VectorStore vectorStore;

    public ProductTools(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Tool(description = "Search for products in the vector store using a search text. Returns the top 3 results.")
    String productSearch(String searchText) {
        log.info("Called productSearch() tool");
        String userId = UserUtils.getUserId();
        String filter = "userId" + "=='" + userId + "'";
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(searchText)
                        .filterExpression(filter)
                        .topK(3)
                        .build());
        if (results != null && results.isEmpty()) {
            log.info("No results found.");
            return "No results found.";
        }
        log.info("Results: " + results);
        return results.toString();
    }
}
