package com.kapital.assignment.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetMessageResponse {
    private Long messageId;
    private String encryptedMessage;
    private String error;
}

