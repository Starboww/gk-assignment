package com.kapital.assignment.message_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EncryptionResponse {
    private String encryptedMessage;
}
