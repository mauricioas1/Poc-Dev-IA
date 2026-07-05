package com.roadcard.dockwebhook.service.impl;

import com.roadcard.dockwebhook.entity.IdempotencyRecord;
import com.roadcard.dockwebhook.repository.IdempotencyRecordRepository;
import com.roadcard.dockwebhook.service.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Primary
public class JpaIdempotencyService implements IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(JpaIdempotencyService.class);

    private final IdempotencyRecordRepository repository;

    public JpaIdempotencyService(IdempotencyRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public boolean register(String key) {
        if (key == null || key.isBlank()) {
            return true;
        }

        if (repository.existsByKey(key)) {
            return false;
        }

        try {
            repository.save(new IdempotencyRecord(key, Instant.now().toEpochMilli()));
            return true;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Idempotency record insert failed, likely duplicate: {}", key);
            return false;
        }
    }
}
