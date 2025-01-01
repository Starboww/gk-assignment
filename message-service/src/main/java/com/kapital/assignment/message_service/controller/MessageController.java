package com.kapital.assignment.message_service.controller;

import com.kapital.assignment.message_service.dto.GetMessageResponse;
import com.kapital.assignment.message_service.dto.SendMessageRequest;
import com.kapital.assignment.message_service.dto.SendMessageResponse;
import com.kapital.assignment.message_service.model.Message;
import com.kapital.assignment.message_service.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<SendMessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request,
                                                           Authentication authentication) throws Exception {
        // Extract user ID from authentication principal
        Integer userId = getUserIdFromAuthentication(authentication);

        // Send the message
        Message message = messageService.sendMessage(request.getMessage(), request.getEncryptionType(), userId);

        // Prepare response
        SendMessageResponse response = new SendMessageResponse("Success", message.getEncryptedMessage(), message.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    /**
     * Endpoint to retrieve a message by ID.
     *
     * @param id The ID of the message.
     * @param authentication The authentication object containing user details.
     * @return ResponseEntity with the encrypted message.
     * @throws Exception if retrieval fails.
     */
    @GetMapping("/{id}")
  //  @PreAuthorize("hasRole('ROLE_message_reader')")
    public ResponseEntity<GetMessageResponse> getMessage(@PathVariable Long id, Authentication authentication) throws Exception {
        // Extract user ID from authentication principal
        Integer userId = getUserIdFromAuthentication(authentication);

        // Retrieve the message
        Message message = messageService.getMessage(id, userId);

        // Prepare response
        GetMessageResponse response = new GetMessageResponse(message.getId(), message.getEncryptedMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Helper method to extract user ID from Authentication object.
     * Assumes that the username corresponds to the user ID in the Authentication Service.
     *
     * @param authentication The authentication object.
     * @return The user ID as Integer.
     * @throws Exception if user ID extraction fails.
     */
    private Integer getUserIdFromAuthentication(Authentication authentication) throws Exception {
        // Assuming that the username is the user ID as a string
        // Adjust this logic based on your Authentication Service's implementation
        String username = authentication.getName();
        try {
            return Integer.parseInt(username);
        } catch (NumberFormatException e) {
            throw new Exception("Invalid user ID in token");
        }
    }


}
