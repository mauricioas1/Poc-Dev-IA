package com.roadcard.dockwebhook.service.impl;

import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DockDecryptServiceTest {

    @Test
    void decrypt_returnsPlaintext_forValidEnvelope() throws Exception {
        // Generate RSA keypair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        // Generate AES key
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        SecretKey aesKey = kg.generateKey();

        String plaintext = "{\"purchase_id\":1234,\"message\":\"hello\"}";

        // Encrypt plaintext with AES-GCM
        byte[] iv = new byte[12];
        SecureRandom rnd = new SecureRandom();
        rnd.nextBytes(iv);

        Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        aes.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] ciphertext = aes.doFinal(plaintext.getBytes());

        // Encrypt AES key with RSA-OAEP
        Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, kp.getPublic());
        byte[] encryptedKey = rsa.doFinal(aesKey.getEncoded());

        String envelope = String.format("{\"encrypted_key\":\"%s\",\"iv\":\"%s\",\"ciphertext\":\"%s\"}",
                Base64.getEncoder().encodeToString(encryptedKey),
                Base64.getEncoder().encodeToString(iv),
                Base64.getEncoder().encodeToString(ciphertext));

        // Write private key to temp PEM file (PKCS8)
        byte[] pkcs8 = kp.getPrivate().getEncoded();
        String pem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(pkcs8) +
                "\n-----END PRIVATE KEY-----\n";

        Path temp = Files.createTempFile("private_key", ".pem");
        Files.writeString(temp, pem);

        DockDecryptService svc = new DockDecryptService();
        // set privateKeyPath via reflection
        var field = DockDecryptService.class.getDeclaredField("privateKeyPath");
        field.setAccessible(true);
        field.set(svc, temp.toString());

        String result = svc.decrypt(envelope);
        assertEquals(plaintext, result);

        // cleanup
        Files.deleteIfExists(temp);
    }
}
