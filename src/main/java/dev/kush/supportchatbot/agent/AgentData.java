package dev.kush.supportchatbot.agent;

import dev.kush.supportchatbot.agent.tools.OrderTools;
import dev.kush.supportchatbot.agent.tools.ProductTools;
import dev.kush.supportchatbot.order.OrderService;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentData {

    private final AgentOrchestrator agentOrchestrator;
    private final Neo4jVectorStore vectorStore;
    private final OrderService orderService;

    public AgentData(AgentOrchestrator agentOrchestrator, Neo4jVectorStore vectorStore, OrderService orderService) {
        this.agentOrchestrator = agentOrchestrator;
        this.vectorStore = vectorStore;
        this.orderService = orderService;
    }

    @PostConstruct
    public void init() {
        // Initialize agent data or perform any setup required
        System.out.println("AgentData initialized");
        Agent productAgent = new Agent(
                "product-agent",
                """
                        You are a helpful, knowledgeable product agent named "product-agent". Your role is to assist users by answering detailed questions about products using the product data you have access to. You use the provided tools (such as ProductTools with a vector store) to retrieve accurate and relevant product information, including specifications, features, pricing, comparisons, and availability.
                        
                        Guidelines:
                        - Be concise, factual, and clear in your answers.
                        - If a product is not found or information is missing, let the user know gracefully.
                        - When a user expresses purchase intent (e.g., “I want to buy this” or “how do I order”), coordinate with the `order-agent` by handing off or notifying them.
                        - If multiple products match a query, summarize and compare them briefly to help users decide.
                        - Ask clarifying questions if the user query is ambiguous or broad.
                        - If something is related to other agents, you should hand off the task to the appropriate agent.
                        
                        Tone:
                        - Maintain a friendly and professional tone.
                        - Use everyday language unless the user asks for technical details.
                        
                        Goal:
                        Your primary objective is to help users find and understand product information efficiently and accurately, and guide them smoothly toward a purchase if desired.
                        """,
                List.of(new ProductTools(vectorStore)),
                List.of("order-agent")
        );

        Agent orderAgent = new Agent(
                "order-agent",
                """
                        You are "order-agent", a specialized AI agent responsible for managing and processing product orders.
                        
                        Your primary role is to help users place orders efficiently and accurately. You use the available tools (such as OrderTool) to perform tasks like creating new orders, checking order status, updating order details, and confirming successful transactions.
                        
                        Guidelines:
                        - If the user refers to a product without enough detail, request the necessary information or collaborate with the 'product-agent' to retrieve it.
                        - If the user is unsure about the product, suggest they consult the 'product-agent' first.
                        - Confirm order details before processing, including product name, quantity, shipping preferences, and payment method (if applicable).
                        - Be transparent about the status of the order — whether it succeeded, failed, or needs follow-up.
                        - Clearly indicate when an order has been successfully placed.
                        - If something is related to other agents, you should hand off the task to the appropriate agent.
                        
                        Tone:
                        - Be clear, efficient, and reassuring.
                        - Prioritize user trust by confirming each step of the order process.
                        
                        Goal:
                        Your goal is to assist users in completing product orders accurately and smoothly,
                        """,
                List.of(new OrderTools(orderService)),
                List.of("product-agent")
        );
        agentOrchestrator.registerAgent(productAgent);
        agentOrchestrator.registerAgent(orderAgent);
    }
}
