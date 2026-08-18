package com.rolmer.inteligent_friend.chat;

import com.anthropic.models.messages.MessageCreateParams;
import com.rolmer.inteligent_friend.config.AnthropicProperties;
import org.springframework.stereotype.Component;

/**
 * Monta o {@link MessageCreateParams} enviado a Messages API, resolvendo
 * o maxTokens efetivo (valor da request ou default de anthropic.max-tokens).
 */
@Component
public class ChatMessageRequestFactory {

    private final AnthropicProperties anthropicProperties;

    public ChatMessageRequestFactory(AnthropicProperties anthropicProperties) {
        this.anthropicProperties = anthropicProperties;
    }

    public MessageCreateParams build(String userMessage, Long requestedMaxTokens) {
        return MessageCreateParams.builder()
                .model(anthropicProperties.model())
                .maxTokens(resolveMaxTokens(requestedMaxTokens))
                .addUserMessage(userMessage)
                .build();
    }

    private long resolveMaxTokens(Long requestedMaxTokens) {
        return requestedMaxTokens != null ? requestedMaxTokens : anthropicProperties.maxTokens();
    }
}
