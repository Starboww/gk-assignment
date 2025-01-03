package com.kapital.assignment.message_service.repo;

import com.kapital.assignment.message_service.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByIdAndUserId(Long id, Long userId);
}
