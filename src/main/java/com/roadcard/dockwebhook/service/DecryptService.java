package com.roadcard.dockwebhook.service;

public interface DecryptService {
    /**
     * Decrypt the received envelope and return the plaintext JSON string.
     */
    String decrypt(String envelope) throws Exception;
}
