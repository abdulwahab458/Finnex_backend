package com.finnex.finance_app.domain.intelligence.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai-test")
@RequiredArgsConstructor
public class AiTestController {

    private final ChatModel chatModel;

    @GetMapping
    public String test(
            @RequestParam(defaultValue = "Say hello in one sentence")
            String message) {

        return chatModel.call(message);
    }
}
