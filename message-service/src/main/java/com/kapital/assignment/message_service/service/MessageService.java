package com.kapital.assignment.message_service.service;

import com.kapital.assignment.message_service.client.EncryptionClient;
import com.kapital.assignment.message_service.dto.EncryptionRequest;
import com.kapital.assignment.message_service.dto.EncryptionResponse;
import com.kapital.assignment.message_service.model.Message;
import com.kapital.assignment.message_service.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EncryptionClient encryptionClient;

    /**
     * Sends a message: Encrypts and stores it.
     *
     * @param messageContent The plaintext message.
     * @param encryptionType The type of encryption (AES/RSA).
     * @param userId         The ID of the user sending the message.
     * @return The stored Message entity.
     * @throws Exception if encryption fails.
     */
    public Message sendMessage(String messageContent, String encryptionType, Integer userId) throws Exception {
        // Call Encryption Service to encrypt the message
        EncryptionRequest encryptionRequest = new EncryptionRequest(messageContent, encryptionType);
        EncryptionResponse encryptionResponse = encryptionClient.encryptMessage(encryptionRequest);

        if (encryptionResponse == null || encryptionResponse.getEncryptedMessage() == null) {
            throw new Exception("Failed to encrypt the message");
        }

        // Create and save the message entity
        Message message = Message.builder()
                .encryptedMessage(encryptionResponse.getEncryptedMessage())
                .encryptionType(encryptionType)
                .userId(userId)
                .build();
        return messageRepository.save(message);
    }

    /**
     * Retrieves a message by ID and user ID.
     *
     * @param messageId The ID of the message.
     * @param userId    The ID of the user requesting the message.
     * @return The Message entity.
     * @throws Exception if message not found or unauthorized access.
     */
    public Message getMessage(Long messageId, Integer userId) throws Exception {
        Optional<Message> optionalMessage = messageRepository.findByIdAndUserId(messageId, userId);
        if (optionalMessage.isEmpty()) {
            throw new Exception("Message not found or unauthorized access");
        }
        return optionalMessage.get();
    }
}
