package com.roadcard.dockwebhook.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseEventSpecificDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private String extractBlock(String markdown, String marker) {
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
    void purchaseApproved_mapsToDto() throws Exception {
        String md = Files.readString(Path.of("Prompt.md"));
        String block = extractBlock(md, "- Purchase_approved");
        assertNotNull(block);
        JsonNode node = mapper.readTree(block);
        PurchaseApprovedDto dto = mapper.treeToValue(node, PurchaseApprovedDto.class);
        assertEquals(151243393L, dto.getPurchaseId());
        assertNotNull(dto.getAmount());
    }

    @Test
    void purchaseProcessed_mapsToDto() throws Exception {
        String md = Files.readString(Path.of("Prompt.md"));
        String block = extractBlock(md, "- Purchase_processed");
        assertNotNull(block);
        JsonNode node = mapper.readTree(block);
        PurchaseProcessedDto dto = mapper.treeToValue(node, PurchaseProcessedDto.class);
        assertEquals(3221L, dto.getPurchaseId());
        assertEquals("Processada", node.get("status").asText());
    }
}
