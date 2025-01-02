package com.kapital.assignment.encryption_service.utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {
    private static final String RSA = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public RSAUtil(String publicKeyStr, String privateKeyStr) throws Exception {
        // Initialize PublicKey and PrivateKey from Base64-encoded strings
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }

    // Utility to generate RSA key pair and get Base64-encoded strings
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(plainText);
    }

}
