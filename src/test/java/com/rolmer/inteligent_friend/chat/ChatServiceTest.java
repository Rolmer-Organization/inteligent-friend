package com.rolmer.inteligent_friend.chat;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.services.blocking.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private AnthropicClient anthropicClient;
    @Mock
    private MessageService messageService;
    @Mock
    private ChatMessageRequestFactory requestFactory;
    @Mock
    private ChatResponseExtractor responseExtractor;
    @Mock
    private MessageCreateParams params;
    @Mock
    private Message response;

    @Test
    void delegaMontagemChamadaEExtracaoParaAsColaboradorasCorretas() {
        ChatService chatService = new ChatService(anthropicClient, requestFactory, responseExtractor);

        when(requestFactory.build("ola", 50L)).thenReturn(params);
        when(anthropicClient.messages()).thenReturn(messageService);
        when(messageService.create(params)).thenReturn(response);
        when(responseExtractor.extractText(response)).thenReturn("resposta da claude");

        String resultado = chatService.sendMessage("ola", 50L);

        assertThat(resultado).isEqualTo("resposta da claude");
        verify(messageService).create(params);
    }
}
