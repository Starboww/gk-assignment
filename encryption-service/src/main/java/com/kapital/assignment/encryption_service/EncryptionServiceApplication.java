package com.kapital.assignment.encryption_service;

import com.kapital.assignment.encryption_service.utils.AESUtil;
import com.kapital.assignment.encryption_service.utils.RSAUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.KeyPair;
import java.util.Base64;

@SpringBootApplication
public class EncryptionServiceApplication {

	public static void main(String[] args)  {
		SpringApplication.run(EncryptionServiceApplication.class, args);
	}


}
