package dev.kush.supportchatbot.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    public ChatSessionService(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    public String create(String userId) {
        String sessionId = UUID.randomUUID().toString();

        ChatSession chatSession = new ChatSession();
        chatSession.setUserId(userId);
        chatSession.setSessionId(sessionId);
        chatSession.setActiveAgent("unknown");

        final ChatSession savedChatSession = chatSessionRepository.save(chatSession);
        return savedChatSession.getSessionId();
    }

    @Transactional
    public void updateActiveAgent(String sessionId, String userId, String agentId) {
        chatSessionRepository.updateActiveAgent(sessionId, userId, agentId);
    }

    @Transactional
    public void updateSessionName(String sessionId, String userId, String name) {
        chatSessionRepository.updateName(sessionId, userId, name);
    }

    public String getActiveAgent(String sessionId, String userId) {
        return chatSessionRepository.getActiveAgent(sessionId, userId);
    }

    public List<ChatSession> getAllSessions(String userId) {
        return chatSessionRepository.findByUserId(userId);
    }

    @Transactional
    public void deleteSession(String sessionId, String userId) {
        chatSessionRepository.deleteBySessionIdAndUserId(sessionId, userId);
    }
}
