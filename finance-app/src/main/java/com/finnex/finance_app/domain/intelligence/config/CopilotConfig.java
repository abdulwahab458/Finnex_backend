package com.finnex.finance_app.domain.intelligence.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CopilotConfig {

    @Bean
    public ChatClient copilotChatClient(
            ChatModel chatModel
    ) {

        return ChatClient
                .builder(chatModel)
                .build();
    }
}
