package com.roadcard.dockwebhook.service;

public interface SnsService {
    /**
     * Publish the given message to SNS. If eventType is provided, it will be set as
     * a Message Attribute `EventType` for consumers.
     */
    void publish(String message, String eventType) throws Exception;
}
