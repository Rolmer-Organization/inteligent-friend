package com.rolmer.inteligent_friend.chat;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fase 2: recebe a mensagem via HTTP e delega o envio a Claude ao
 * ClaudeChatService. A mensagem deixa de ser fixa e passa a vir do corpo
 * da requisicao.
 */
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService claudeChatService;

    public ChatController(ChatService claudeChatService) {
        this.claudeChatService = claudeChatService;
    }

    @PostMapping
    public ChatResponse sendMessage(@Valid @RequestBody ChatRequest request) {
        String response = claudeChatService.sendMessage(request.message(), request.maxTokens());
        return new ChatResponse(response);
    }
}
