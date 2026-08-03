package com.finnex.finance_app.domain.intelligence.controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.intelligence.dto.CopilotChatRequest;
import com.finnex.finance_app.domain.intelligence.dto.CopilotChatResponse;
import com.finnex.finance_app.domain.intelligence.service.CopilotService;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/copilot")
@RequiredArgsConstructor
public class CopilotController {

    private final CopilotService copilotService;

    @PostMapping("/chat")
    public ApiResponse<CopilotChatResponse> chat(
            @Valid @RequestBody CopilotChatRequest request) {


        CopilotChatResponse response =
                copilotService.chat(request.getMessage());

        return ApiResponse.ok(
                response,
                "Copilot response generated"
        );
    }
}
