package com.rolmer.inteligent_friend.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao geral da aplicacao.
 *
 * Habilita o binding automatico de qualquer classe anotada com
 * @ConfigurationProperties para os valores do application.yaml. O Spring
 * resolve essas propriedades combinando o application.yaml (base) com o
 * arquivo do perfil ativo (ex.: application-dev.yaml, application-prod.yaml)
 * - o arquivo de perfil sobrescreve o valor base para o ambiente em questao.
 *
 * Por usar @ConfigurationPropertiesScan em vez de registrar cada classe
 * manualmente, novas propriedades tipadas podem ser adicionadas no futuro
 * (ex.: propriedades de um novo modulo) sem precisar alterar esta classe.
 */
@Configuration
@ConfigurationPropertiesScan(basePackages = "com.rolmer.inteligent_friend")
public class GeneralConfig {
}
