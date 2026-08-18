package com.rolmer.inteligent_friend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Propriedades da integracao com a Anthropic, lidas do application.yaml.
 * O valor efetivo depende do perfil ativo: application-dev.yaml e
 * application-prod.yaml podem sobrescrever qualquer campo aqui declarado
 * para o ambiente correspondente.
 */
@ConfigurationProperties(prefix = "anthropic")
public record AnthropicProperties(
        @DefaultValue("claude-opus-5") String model,
        @DefaultValue("1024") Long maxTokens) {
}
