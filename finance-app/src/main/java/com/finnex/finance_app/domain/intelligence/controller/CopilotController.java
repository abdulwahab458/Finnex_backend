package com.finnex.finance_app.domain.intelligence.controller;

import com.finnex.finance_app.common.ratelimit.Service.RateLimitService;
import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.intelligence.dto.CopilotChatRequest;
import com.finnex.finance_app.domain.intelligence.dto.CopilotChatResponse;
import com.finnex.finance_app.domain.intelligence.service.CopilotService;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/copilot")
@RequiredArgsConstructor
public class CopilotController {

    private final CopilotService copilotService;
    private final RateLimitService rateLimitService;

    @PostMapping("/chat")
    public ApiResponse<CopilotChatResponse> chat(
            @Valid @RequestBody CopilotChatRequest request,
            HttpServletRequest httpsrequest
            ) {
        rateLimitService.validateRequest(
                httpsrequest,
                "chat",
                15,
                Duration.ofDays(1)
        );
        CopilotChatResponse response =
                copilotService.chat(request.getMessage());

        return ApiResponse.ok(
                response,
                "Copilot response generated"
        );
    }
}
