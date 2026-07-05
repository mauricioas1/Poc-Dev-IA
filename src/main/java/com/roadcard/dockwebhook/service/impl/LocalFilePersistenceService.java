package com.roadcard.dockwebhook.service.impl;

import com.roadcard.dockwebhook.service.FilePersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Service
public class LocalFilePersistenceService implements FilePersistenceService {

    private static final Logger log = LoggerFactory.getLogger(LocalFilePersistenceService.class);

    @Value("${dock.storage.path:data/envelopes}")
    private String storagePath;

    @Override
    public void saveRawEnvelope(String raw) throws IOException {
        Path dir = Path.of(storagePath);
        Files.createDirectories(dir);
        String file = String.format("envelope-%d.json", Instant.now().toEpochMilli());
        Path target = dir.resolve(file);
        Files.writeString(target, raw == null ? "" : raw);
        log.info("Saved raw envelope to {}", target.toAbsolutePath());
    }
}
