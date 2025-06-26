package dev.kush.supportchatbot.chat;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import java.util.Map;

public record ChatMessage(String agent, String content) implements Message {

    @Override
    public MessageType getMessageType() {
        return MessageType.USER;
    }

    @Override
    public String getText() {
        return content;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of();
    }
}