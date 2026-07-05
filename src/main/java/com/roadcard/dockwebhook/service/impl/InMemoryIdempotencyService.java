package com.roadcard.dockwebhook.service.impl;

import com.roadcard.dockwebhook.service.IdempotencyService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryIdempotencyService implements IdempotencyService {

    private final Map<String, Long> processed = new ConcurrentHashMap<>();

    @Override
    public boolean register(String key) {
        if (key == null || key.isBlank()) {
            return true; // cannot dedupe without key - allow processing
        }
        Long now = Instant.now().toEpochMilli();
        return processed.putIfAbsent(key, now) == null;
    }
}
