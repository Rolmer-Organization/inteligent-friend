package com.rolmer.inteligent_friend.chat;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.TextBlock;
import com.rolmer.inteligent_friend.config.AnthropicProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Encapsula a comunicacao com a Messages API da Anthropic.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final AnthropicClient anthropicClient;
    private final AnthropicProperties anthropicProperties;

    public ChatService(AnthropicClient anthropicClient, AnthropicProperties anthropicProperties) {
        this.anthropicClient = anthropicClient;
        this.anthropicProperties = anthropicProperties;
    }

    /**
     * @param requestedMaxTokens limite de tokens de saida vindo da request;
     *                           se null, usa o default de anthropic.max-tokens
     */
    public String sendMessage(String userMessage, Long requestedMaxTokens) {
        long maxTokens = resolveMaxTokens(requestedMaxTokens);
        log.debug("Enviando mensagem para o modelo {} (maxTokens={})", anthropicProperties.model(), maxTokens);

        MessageCreateParams params = MessageCreateParams.builder()
                .model(anthropicProperties.model())
                .maxTokens(maxTokens)
                .addUserMessage(userMessage)
                .build();

        Message response = anthropicClient.messages().create(params);

        return response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(TextBlock::text)
                .collect(Collectors.joining());
    }

    private long resolveMaxTokens(Long requestedMaxTokens) {
        return requestedMaxTokens != null ? requestedMaxTokens : anthropicProperties.maxTokens();
    }
}
