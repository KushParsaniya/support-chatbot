package dev.kush.supportchatbot.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.data.annotation.PersistenceCreator;

@Entity
public class ChatSession {

    @Id
    @GeneratedValue
    private Long id;

    private String sessionId;

    private String userId;

    private String name;

    private String activeAgent;

    @PersistenceCreator
    public ChatSession(Long id, String sessionId, String userId, String name, String activeAgent) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.name = name;
        this.activeAgent = activeAgent;
    }

    public ChatSession() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActiveAgent() {
        return activeAgent;
    }

    public void setActiveAgent(String activeAgent) {
        this.activeAgent = activeAgent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}