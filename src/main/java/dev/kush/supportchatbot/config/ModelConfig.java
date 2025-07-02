package dev.kush.supportchatbot.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ModelConfig {

    private final OpenAiProperty openAiProperty;
    private final OllamaProperty ollamaProperty;
    private final AzureAiConfig azureAiConfig;

    public ModelConfig(OpenAiProperty openAiProperty, OllamaProperty ollamaProperty, AzureAiConfig azureAiConfig) {
        this.openAiProperty = openAiProperty;
        this.ollamaProperty = ollamaProperty;
        this.azureAiConfig = azureAiConfig;
    }

    @Bean(name = "opnAiChatModel")
    @Primary
    ChatModel openAiChatModel() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(openAiProperty.getBaseUrl())
                .apiKey(openAiProperty.getApiKey())
                .build();
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(openAiProperty.getModel())
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(openAiChatOptions)
                .build();
    }

    @Bean(name = "ollamaChatModel")
    ChatModel ollamaChatModel() {
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(ollamaProperty.getBaseUrl())
                .build();
        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model(ollamaProperty.getChatModel())
                .build();
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions)
                .build();
    }

    @Bean(name = "azureChatModel")
    ChatModel azureChatModel() {
        var openAIClientBuilder = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(azureAiConfig.getApiKey()))
                .endpoint(azureAiConfig.getBaseUrl());

        var openAIChatOptions = AzureOpenAiChatOptions.builder()
                .deploymentName(azureAiConfig.getChatModel())
                .build();

        return AzureOpenAiChatModel.builder()
                .openAIClientBuilder(openAIClientBuilder)
                .defaultOptions(openAIChatOptions)
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(ollamaProperty.getBaseUrl())
                .build();
        OllamaOptions ollamaOptions = OllamaOptions.builder()
                .model(ollamaProperty.getEmbeddingModel())
                .build();
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(ollamaOptions)
                .build();
    }
}
