package dev.kush.supportchatbot.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    @Modifying
    @Query("update ChatSession cs set cs.activeAgent = :activeAgent where cs.sessionId = :sessionId and cs.userId = :userId")
    void updateActiveAgent(String sessionId, String userId, String activeAgent);

    @Modifying
    @Query("update ChatSession cs set cs.name = :name where cs.sessionId = :sessionId and cs.userId = :userId")
    void updateName(String sessionId, String userId, String name);

    @Query("select cs.activeAgent from ChatSession cs where cs.sessionId = :sessionId and cs.userId = :userId")
    String getActiveAgent(String sessionId, String userId);

    @Query("select cs from ChatSession cs where cs.userId = :userId")
    List<ChatSession> findByUserId(String userId);

    @Modifying
    @Query("delete from ChatSession cs where cs.sessionId = :sessionId and cs.userId = :userId")
    void deleteBySessionIdAndUserId(String sessionId, String userId);
}