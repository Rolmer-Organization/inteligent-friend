package com.rolmer.inteligent_friend.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Corpo da requisicao HTTP para enviar uma mensagem a Claude.
 *
 * maxTokens e opcional: quando omitida (null), o valor default configurado
 * em anthropic.max-tokens (application.yaml) e usado.
 */
public record ChatRequest(
        @NotBlank(message = "A mensagem nao pode estar vazia") String message,
        @Positive(message = "maxTokens deve ser maior que zero") Long maxTokens) {
}
