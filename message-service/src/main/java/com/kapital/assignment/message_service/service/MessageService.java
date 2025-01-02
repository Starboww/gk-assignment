package com.kapital.assignment.message_service.service;

import com.kapital.assignment.message_service.client.EncryptionClient;
import com.kapital.assignment.message_service.config.CustomUserDetails;
import com.kapital.assignment.message_service.dto.EncryptionRequest;
import com.kapital.assignment.message_service.dto.EncryptionResponse;
import com.kapital.assignment.message_service.model.Message;
import com.kapital.assignment.message_service.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EncryptionClient encryptionServiceClient;


    public Message sendMessage(String messageContent, String encryptionType, Long userId) throws Exception {
        // Call Encryption Service to encrypt the message
        EncryptionRequest encryptionRequest = new EncryptionRequest(messageContent, encryptionType);
        EncryptionResponse encryptionResponse = encryptionServiceClient.encryptMessage(encryptionRequest);

        if (encryptionResponse == null || encryptionResponse.getEncryptedMessage() == null) {
            throw new Exception("Failed to encrypt the message");
        }
        // Save the encrypted message
        Message message = Message.builder()
                .encryptedMessage(encryptionResponse.getEncryptedMessage())
                .originalMessage(messageContent)
                .encryptionType(encryptionType.toUpperCase())
                .userId(userId)
                .createdAt(ZonedDateTime.from(Instant.now()))
                .build();

        return messageRepository.save(message);
    }

    public Optional<Message> getMessage(Long messageId, Long userId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent() && messageOpt.get().getUserId().equals(userId)) {
            return messageOpt;
        }
        return Optional.empty();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) throws Exception {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }
}
