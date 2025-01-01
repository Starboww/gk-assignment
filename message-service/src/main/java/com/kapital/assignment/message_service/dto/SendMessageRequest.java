package com.kapital.assignment.message_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {
    @NotBlank(message = "Message cannot be blank")
    private String message;

    @NotBlank(message = "Encryption type cannot be blank")
    @Pattern(regexp = "AES|RSA", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Encryption type must be AES or RSA")
    private String encryptionType;

}
