package dev.kush.supportchatbot.agent;

import dev.kush.supportchatbot.agent.tools.AgentTransfer;
import dev.kush.supportchatbot.chat.ChatMessage;
import dev.kush.supportchatbot.chat.ChatSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentOrchestrator {
    private final ChatSessionService chatSessionService;
    private Map<String, Agent> agents = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(AgentOrchestrator.class);

    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    public AgentOrchestrator(@Qualifier("ollamaChatModel") ChatModel ollamaChatModel, @Qualifier("opnAiChatModel") ChatModel openAiChatModel,
                             VectorStore vectorStore, ChatSessionService chatSessionService) {
        this.openAiChatClient = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), VectorStoreChatMemoryAdvisor.builder(vectorStore).build())
                .build();
        this.ollamaChatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.chatSessionService = chatSessionService;
    }

    public void registerAgent(Agent agent) {
        agents.put(agent.name(), agent);
    }

    public List<ChatMessage> complete(String input, String sessionId, String userId) {
        String activeAgent = chatSessionService.getActiveAgent(sessionId, userId);
        List<ChatMessage> responseMessages = new ArrayList<>();

        AgentTransfer agentTransfer = new AgentTransfer(chatSessionService, sessionId, userId);

        if (activeAgent.equalsIgnoreCase("unknown")) {
            // first chat
            chatSessionService.updateSessionName(sessionId, userId, summarize(input));
            activeAgent = routeAgent(input);
            agentTransfer.transferAgent(activeAgent);
        }

        Agent agent = agents.get(activeAgent);
        agentTransfer.setRoutableAgents(agent.rotableAgents());

        List<Object> tools = new ArrayList<>();
        for (Object tool : agent.tools()) {
            tools.add(tool);
        }
        tools.add(agentTransfer);

        if (agent == null) {
            log.error("Agent not found: {}", activeAgent);
            return List.of();
        }
        String response = openAiChatClient.prompt()
                .system(agent.systemPrompt())
                .user(input)
                .tools(tools.toArray())
                .call()
                .content();

        String checkActiveAgent = chatSessionService.getActiveAgent(sessionId, userId);

        responseMessages.add(new ChatMessage(activeAgent, response));

        if (!checkActiveAgent.equals(activeAgent)) {
            // agent is changed
            log.info("Agent transfer during processing. New agent: {}", checkActiveAgent);
            List<ChatMessage> recursiveResponse = complete(input, sessionId, userId);
            responseMessages.addAll(recursiveResponse);
        }
        return responseMessages;

    }

    private String routeAgent(String input) {

        String prompt = String.format("""
                 You are a support query classifier. Your task is to analyze the user input and route it to the most appropriate support team from the following list:
                 %s
                
                 Follow these steps:
                
                 1. Carefully analyze the input text and identify key terms, intent, and urgency.
                 2. Use this analysis to determine the most relevant support team.
                 3. If no clear team matches the request, assign "unknown".
                
                 Respond ONLY in this JSON format:
                
                 {
                     "reasoning": "Brief explanation of your classification decision based on input keywords, intent, and urgency.",
                     "selection": "The name of the most appropriate support team, or 'unknown' if uncertain."
                 }
                 Input:
                 %s
                """, String.join(", ", agents.keySet()), input);

        final RoutingDecision routingDecision = ollamaChatClient.prompt(prompt)
                .call()
                .entity(RoutingDecision.class);

        log.info("Routing decision: {}", routingDecision);
        return routingDecision.selection();
    }

    // you can make this async if needed or call in separate thread
    private String summarize(String userMessage) {
        String prompt = "Summarize this message in 4-6 words as a session title: " + userMessage;

        String response = ollamaChatClient.prompt(prompt).call().content();
        if (response == null || response.isBlank()) {
            response = "No summary available";
        } else {
            response = response.replaceAll("[\"\n]", "").trim();
        }
        // save session summary to database
        log.info("Session summary: {}", response);
        return response;
    }

    record RoutingDecision(String reasoning, String selection) {
    }


}
