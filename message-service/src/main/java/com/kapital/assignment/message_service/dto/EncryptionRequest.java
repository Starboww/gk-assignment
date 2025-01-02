package com.kapital.assignment.message_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EncryptionRequest {
    private String message;
    private String encryptionType;

}
