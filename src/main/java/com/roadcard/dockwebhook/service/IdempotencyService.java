package com.roadcard.dockwebhook.service;

public interface IdempotencyService {
    /**
     * Register a key and return true if it was newly registered (not duplicate).
     */
    boolean register(String key);
}
