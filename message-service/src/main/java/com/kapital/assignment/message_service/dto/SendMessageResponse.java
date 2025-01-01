package com.kapital.assignment.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SendMessageResponse {
    private String status;
    private String encryptedMessage;
    private Long messageId;



}
