package com.kapital.assignment.message_service.controller;

import com.kapital.assignment.message_service.config.CustomUserDetails;
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
    @PreAuthorize("hasRole('MESSAGE_WRITER')")
    public ResponseEntity<SendMessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication) {
        try {
            // Extract userId from CustomUserDetails
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();

            Message message = messageService.sendMessage(request.getMessage(), request.getEncryptionType(), userId);
            return ResponseEntity.ok(SendMessageResponse.builder()
                    .status("SUCCESS")
                    .encryptedMessage(message.getEncryptedMessage())
                    .messageId(message.getId()).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SendMessageResponse.builder()
                    .status("FAILURE")
                    .error(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MESSAGE_READER')")
    public ResponseEntity<GetMessageResponse> getMessage(
            @PathVariable("id") Long id,
            Authentication authentication) {
        try {
            // Extract userId from CustomUserDetails
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId();
            return messageService.getMessage(id, userId)
                    .map(message -> ResponseEntity
                            .ok(GetMessageResponse.builder()
                                    .messageId(message.getId())
                                    .encryptedMessage(message.getEncryptedMessage())
                                    .build()))

                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(GetMessageResponse.builder()
                                    .error("Message not found")
                                    .build()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(GetMessageResponse.builder().error(e.getMessage()).build());
        }
    }


}
