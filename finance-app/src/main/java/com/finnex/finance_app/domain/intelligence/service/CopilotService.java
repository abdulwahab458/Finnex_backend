package com.finnex.finance_app.domain.intelligence.service;

import com.finnex.finance_app.domain.intelligence.dto.CopilotChatResponse;

import java.util.UUID;

public interface CopilotService {
    CopilotChatResponse chat( String message);
}
