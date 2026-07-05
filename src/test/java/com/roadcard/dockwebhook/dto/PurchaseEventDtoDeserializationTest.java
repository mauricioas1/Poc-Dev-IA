package com.roadcard.dockwebhook.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PurchaseEventDtoDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private String extractJsonBlock(String markdown, String marker) {
        int payloadSectionIndex = markdown.indexOf("## Payloads dos eventos");
        int markerIndex = markdown.indexOf(marker, payloadSectionIndex == -1 ? 0 : payloadSectionIndex);
        if (markerIndex == -1) {
            markerIndex = markdown.indexOf(marker);
        }
        if (markerIndex == -1) {
            return null;
        }

        int start = markdown.indexOf('{', markerIndex);
        if (start == -1) {
            return null;
        }

        int depth = 0;
        boolean inString = false;
        char previous = 0;
        for (int i = start; i < markdown.length(); i++) {
            char current = markdown.charAt(i);
            if (current == '"' && previous != '\\') {
                inString = !inString;
            }
            if (!inString) {
                if (current == '{') {
                    depth++;
                } else if (current == '}') {
                    depth--;
                    if (depth == 0) {
                        return markdown.substring(start, i + 1);
                    }
                }
            }
            previous = current;
        }
        return null;
    }

    @Test
    void deserializePurchaseApprovedExample() throws Exception {
        String json = Files.readString(Path.of("Prompt.md"));
        String block = extractJsonBlock(json, "- Purchase_approved");
        assertNotNull(block);

        PurchaseEventDto dto = mapper.readValue(block, PurchaseEventDto.class);
        assertEquals(151243393L, dto.getPurchaseId());
    }
}
