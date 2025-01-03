package com.kapital.assignment.encryption_service.service;

import com.kapital.assignment.encryption_service.exception.DecryptionException;
import com.kapital.assignment.encryption_service.exception.InvalidEncryptionTypeException;
import com.kapital.assignment.encryption_service.utils.AESUtil;
import com.kapital.assignment.encryption_service.utils.RSAUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    @Value("${encryption.aes.key}")
    private String aesKeyBase64;

    @Value("${encryption.rsa.public-key}")
    private String rsaPublicKeyBase64;

    @Value("${encryption.rsa.private-key}")
    private String rsaPrivateKeyBase64;

    private AESUtil aesUtil;
    private RSAUtil rsaUtil;

    @PostConstruct
    public void init() throws Exception {

        aesUtil = new AESUtil(aesKeyBase64);

        rsaUtil = new RSAUtil(rsaPublicKeyBase64, rsaPrivateKeyBase64);
    }

    public String encrypt(String message, String encryptionType) throws Exception {
        if ("AES".equalsIgnoreCase(encryptionType)) {
            return aesUtil.encrypt(message);
        } else if ("RSA".equalsIgnoreCase(encryptionType)) {
            return rsaUtil.encrypt(message);
        } else {
            throw new IllegalArgumentException("Unsupported encryption type: " + encryptionType);
        }
    }

    public String decrypt(String encryptedMessage, String encryptionType) throws Exception {
        try {
            if ("AES".equalsIgnoreCase(encryptionType)) {
                return aesUtil.decrypt(encryptedMessage);
            } else if ("RSA".equalsIgnoreCase(encryptionType)) {
                return rsaUtil.decrypt(encryptedMessage);
            } else {
                throw new InvalidEncryptionTypeException("Unsupported decryption type: " + encryptionType);
            }
        } catch (Exception e) {
            throw new DecryptionException("Failed to decrypt the message", e);
        }
    }

}
