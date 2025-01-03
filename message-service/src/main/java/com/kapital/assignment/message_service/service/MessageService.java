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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EncryptionClient encryptionServiceClient;

    /**
     * Sends a message by encrypting it and saving the encrypted version.
     *
     * @param messageContent the content of the message to send
     * @param encryptionType the encryption type to use (e.g., "AES" or "RSA")
     * @param userId         the ID of the user sending the message
     * @return the saved {@link Message} entity
     * @throws Exception if encryption fails or the encrypted message is null
     */
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
                .createdAt(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))
                .build();

        return messageRepository.save(message);
    }

    /**
     * Retrieves a message by its ID and user ID.
     *
     * @param messageId the ID of the message to retrieve
     * @param userId    the ID of the user owning the message
     * @return an {@link Optional} containing the {@link Message} if found, or empty if not found
     */
    public Optional<Message> getMessage(Long messageId, Long userId) {
        return messageRepository.findByIdAndUserId(messageId,userId);
    }


    /**
     * Extracts the user ID from the given authentication details.
     *
     * @param authentication the authentication object containing user details
     * @return the user ID extracted from the authentication object
     * @throws Exception if the authentication details are invalid or missing
     */
    private Long getUserIdFromAuthentication(Authentication authentication) throws Exception {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }
}
