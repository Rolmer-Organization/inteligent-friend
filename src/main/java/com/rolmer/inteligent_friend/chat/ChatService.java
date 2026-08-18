package com.rolmer.inteligent_friend.chat;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orquestra o envio de uma mensagem a Claude: delega a montagem do request
 * a {@link ChatMessageRequestFactory}, chama a Messages API e delega a
 * extracao do texto a {@link ChatResponseExtractor}.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final AnthropicClient anthropicClient;
    private final ChatMessageRequestFactory requestFactory;
    private final ChatResponseExtractor responseExtractor;

    public ChatService(AnthropicClient anthropicClient,
                        ChatMessageRequestFactory requestFactory,
                        ChatResponseExtractor responseExtractor) {
        this.anthropicClient = anthropicClient;
        this.requestFactory = requestFactory;
        this.responseExtractor = responseExtractor;
    }

    public String sendMessage(String userMessage, Long requestedMaxTokens) {
        MessageCreateParams params = requestFactory.build(userMessage, requestedMaxTokens);
        log.debug("Enviando mensagem para o modelo {}", params.model());

        Message response = anthropicClient.messages().create(params);

        return responseExtractor.extractText(response);
    }
}
