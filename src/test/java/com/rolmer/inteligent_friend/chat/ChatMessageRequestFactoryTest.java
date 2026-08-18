package com.rolmer.inteligent_friend.chat;

import com.anthropic.models.messages.MessageCreateParams;
import com.rolmer.inteligent_friend.config.AnthropicProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageRequestFactoryTest {

    private final AnthropicProperties properties = new AnthropicProperties("claude-opus-5", 1024L);
    private final ChatMessageRequestFactory factory = new ChatMessageRequestFactory(properties);

    @Test
    void usaMaxTokensDaRequestQuandoInformado() {
        MessageCreateParams params = factory.build("ola", 50L);

        assertThat(params.maxTokens()).isEqualTo(50L);
        assertThat(params.model().asString()).isEqualTo("claude-opus-5");
    }

    @Test
    void usaMaxTokensDefaultQuandoRequestNaoInforma() {
        MessageCreateParams params = factory.build("ola", null);

        assertThat(params.maxTokens()).isEqualTo(1024L);
    }
}
