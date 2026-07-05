package com.roadcard.dockwebhook.service;

public interface FilePersistenceService {
    void saveRawEnvelope(String raw) throws Exception;
}
