package com.kapital.assignment.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DecryptionResponse {

    private String decryptedMessage;

}