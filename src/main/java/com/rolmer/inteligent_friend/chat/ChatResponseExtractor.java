package com.rolmer.inteligent_friend.chat;

import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.TextBlock;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Extrai o texto de uma {@link Message} retornada pela Messages API,
 * concatenando todos os blocos de texto da resposta.
 */
@Component
public class ChatResponseExtractor {

    public String extractText(Message response) {
        return response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(TextBlock::text)
                .collect(Collectors.joining());
    }
}
