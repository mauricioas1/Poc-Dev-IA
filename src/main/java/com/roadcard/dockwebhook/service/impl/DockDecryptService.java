package com.roadcard.dockwebhook.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadcard.dockwebhook.service.DecryptService;
import com.roadcard.dockwebhook.service.exception.DecryptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class DockDecryptService implements DecryptService {

    private static final Logger log = LoggerFactory.getLogger(DockDecryptService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${dock.private-key-path:private_key.pem}")
    private String privateKeyPath;

    @Override
    public String decrypt(String envelope) throws Exception {
        // Expect envelope JSON with fields: encrypted_key, iv, ciphertext (all Base64)
        JsonNode root;
        try {
            root = mapper.readTree(envelope);
        } catch (IOException e) {
            throw new DecryptException("Invalid envelope JSON", e);
        }

        if (!root.hasNonNull("encrypted_key") || !root.hasNonNull("iv") || !root.hasNonNull("ciphertext")) {
            throw new DecryptException("Envelope missing required fields (encrypted_key, iv, ciphertext)");
        }

        byte[] encryptedKey = Base64.getDecoder().decode(root.get("encrypted_key").asText());
        byte[] iv = Base64.getDecoder().decode(root.get("iv").asText());
        byte[] ciphertext = Base64.getDecoder().decode(root.get("ciphertext").asText());

        PrivateKey privateKey = loadPrivateKey(privateKeyPath);

        // Decrypt AES key with RSA
        byte[] aesKey;
        try {
            Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            rsa.init(Cipher.DECRYPT_MODE, privateKey);
            aesKey = rsa.doFinal(encryptedKey);
        } catch (Exception e) {
            log.error("RSA unwrap failed: {}", e.getMessage());
            throw new DecryptException("RSA decrypt failed", e);
        }

        // AES-GCM decrypt
        try {
            Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            aes.init(Cipher.DECRYPT_MODE, keySpec, spec);
            byte[] plain = aes.doFinal(ciphertext);
            return new String(plain);
        } catch (Exception e) {
            log.error("AES decrypt failed: {}", e.getMessage());
            throw new DecryptException("AES decrypt failed", e);
        }
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Path.of(path));
        String pem = new String(keyBytes)
            .replaceAll("-----BEGIN (.*)-----", "")
            .replaceAll("-----END (.*)-----", "")
            .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
}
