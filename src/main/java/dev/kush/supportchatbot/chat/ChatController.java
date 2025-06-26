package dev.kush.supportchatbot.chat;

import dev.kush.supportchatbot.agent.AgentOrchestrator;
import dev.kush.supportchatbot.config.UserUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatSessionService chatSessionService;
    private final AgentOrchestrator agentOrchestrator;

    record ChatCmd(String sessionId, String query) {
    }

    public ChatController(ChatSessionService chatSessionService, AgentOrchestrator agentOrchestrator) {
        this.chatSessionService = chatSessionService;
        this.agentOrchestrator = agentOrchestrator;
    }

    @PostMapping("/init")
    String initChat() {
        String userId = UserUtils.getUserId();
        return chatSessionService.create(userId);
    }
    @PostMapping("/completion")
    String completion(@RequestBody ChatCmd chatCmd) {
        var messages = agentOrchestrator.complete(chatCmd.query, chatCmd.sessionId, UserUtils.getUserId());

        StringBuilder response = new StringBuilder();

        messages.forEach(message -> {
            response.append(message.agent())
                    .append(": ")
                    .append(message.content())
                    .append("\n");
        });
        return response.toString();
    }
}
