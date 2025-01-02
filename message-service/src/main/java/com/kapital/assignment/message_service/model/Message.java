package com.kapital.assignment.message_service.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "messages")
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_message", nullable = false, columnDefinition = "TEXT")
    private String originalMessage;

    @Column(name = "encrypted_message", nullable = false, columnDefinition = "TEXT")
    private String encryptedMessage;

    @Column(name = "encryption_type", nullable = false, length = 10)
    private String encryptionType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

}
