package com.kapital.assignment.encryption_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecryptionRequest {
    @NotBlank(message = "Encrypted message cannot be blank")
    private String encryptedMessage;

    @NotBlank(message = "Encryption type cannot be blank")
    @Pattern(regexp = "AES|RSA", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Encryption type must be AES or RSA")
    private String encryptionType;
}
