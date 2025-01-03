package com.kapital.assignment.encryption_service.controller;

import com.kapital.assignment.encryption_service.config.CustomUserDetails;
import com.kapital.assignment.encryption_service.dto.DecryptionRequest;
import com.kapital.assignment.encryption_service.dto.DecryptionResponse;
import com.kapital.assignment.encryption_service.dto.EncryptionRequest;
import com.kapital.assignment.encryption_service.dto.EncryptionResponse;
import com.kapital.assignment.encryption_service.service.EncryptionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/encrypt")
public class EncryptionController {
    @Autowired
    private EncryptionService encryptionService;

    @PostMapping
    @PreAuthorize("hasRole('MESSAGE_WRITER')")
    public ResponseEntity<EncryptionResponse> encryptMessage(
            @Valid @RequestBody EncryptionRequest request,
            Authentication authentication) throws Exception {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        log.debug("User Id is: {}", userId);
        String encryptedMessage = encryptionService.encrypt(request.getMessage(), request.getEncryptionType());
        return new ResponseEntity<>(new EncryptionResponse(encryptedMessage), HttpStatus.OK);
    }

    @PostMapping("/decrypt")
    @PreAuthorize("hasRole('MESSAGE_READER')")
    public ResponseEntity<DecryptionResponse> decryptMessage(
            @Valid @RequestBody DecryptionRequest request) throws Exception {
        String decryptedMessage = encryptionService.decrypt(
                request.getEncryptedMessage(),
                request.getEncryptionType()
        );
        return ResponseEntity.ok(new DecryptionResponse(decryptedMessage));
    }
}
