package com.rolmer.inteligent_friend.chat;

import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.TextBlock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatResponseExtractorTest {

    private final ChatResponseExtractor extractor = new ChatResponseExtractor();

    @Test
    void concatenaTodosOsBlocosDeTexto() {
        ContentBlock bloco1 = ContentBlock.ofText(TextBlock.builder().text("Ola, ").citations(List.of()).build());
        ContentBlock bloco2 = ContentBlock.ofText(TextBlock.builder().text("mundo!").citations(List.of()).build());
        Message response = mock(Message.class);
        when(response.content()).thenReturn(List.of(bloco1, bloco2));

        String texto = extractor.extractText(response);

        assertThat(texto).isEqualTo("Ola, mundo!");
    }

    @Test
    void devolveVazioQuandoNaoHaBlocoDeTexto() {
        Message response = mock(Message.class);
        when(response.content()).thenReturn(List.of());

        String texto = extractor.extractText(response);

        assertThat(texto).isEmpty();
    }
}
