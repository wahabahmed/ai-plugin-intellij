package com.turbo.dev.plugin.dto.chat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRequest {
    private String prompt;
}
