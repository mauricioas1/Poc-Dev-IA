package com.roadcard.dockwebhook.service;

import com.roadcard.dockwebhook.service.impl.InMemoryIdempotencyService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryIdempotencyServiceTest {

    @Test
    void register_returnsTrue_whenNew() {
        InMemoryIdempotencyService svc = new InMemoryIdempotencyService();
        assertTrue(svc.register("key-1"));
        assertFalse(svc.register("key-1"));
    }

    @Test
    void register_allowsNullOrBlankKey() {
        InMemoryIdempotencyService svc = new InMemoryIdempotencyService();
        assertTrue(svc.register(""));
        assertTrue(svc.register(null));
    }
}
