package dev.kush.supportchatbot.agent.tools;

import dev.kush.supportchatbot.chat.ChatSessionService;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

public class AgentTransfer {

    private final ChatSessionService chatSessionService;
    private List<String> routableAgents;
    private final String sessionId;
    private final String userId;

    public AgentTransfer(ChatSessionService chatSessionService, String sessionId, String userId) {
        this.chatSessionService = chatSessionService;
        this.sessionId = sessionId;
        this.userId = userId;
    }

    @Tool(description = "Get routable agents")
    public List<String> getRoutableAgents() {
        return routableAgents;
    }

    public void setRoutableAgents(List<String> routableAgents) {
        this.routableAgents = routableAgents;
    }

    @Tool(description = "Transfer the chat session to a different agent call this when you want to transfer the chat session to a different agent")
    public String transferAgent(String agentName) {
        chatSessionService.updateActiveAgent(sessionId, userId, agentName);
        return "Agent transferred to " + agentName;
    }

}