package com.kapital.assignment.message_service.client;

import com.kapital.assignment.message_service.config.EncryptionServiceFeignClientConfig;
import com.kapital.assignment.message_service.dto.DecryptionRequest;
import com.kapital.assignment.message_service.dto.DecryptionResponse;
import com.kapital.assignment.message_service.dto.EncryptionRequest;
import com.kapital.assignment.message_service.dto.EncryptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "encryption-service", url = "${encryption.service.url}", configuration = EncryptionServiceFeignClientConfig.class)
public interface EncryptionClient {
    @PostMapping("/api/encrypt")
    EncryptionResponse encryptMessage(@RequestBody EncryptionRequest request);

    @PostMapping("/api/encrypt/decrypt")
    DecryptionResponse decryptMessage(@RequestBody DecryptionRequest request);
}
