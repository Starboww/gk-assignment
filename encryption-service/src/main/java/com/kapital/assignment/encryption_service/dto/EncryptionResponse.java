package com.kapital.assignment.encryption_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EncryptionResponse {
    private String encryptedMessage;
}
