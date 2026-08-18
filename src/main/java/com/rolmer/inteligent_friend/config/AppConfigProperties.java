package com.rolmer.inteligent_friend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix = "app")
public record AppConfigProperties(String testProperty) {
}
