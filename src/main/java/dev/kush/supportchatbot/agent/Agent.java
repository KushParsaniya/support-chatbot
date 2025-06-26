package dev.kush.supportchatbot.agent;

import java.util.List;

public record Agent(
        String name,
        String systemPrompt,
        List<Object> tools,
        List<String> rotableAgents
) {
}
