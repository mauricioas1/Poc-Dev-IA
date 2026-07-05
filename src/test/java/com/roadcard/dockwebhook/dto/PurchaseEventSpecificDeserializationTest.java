package com.roadcard.dockwebhook.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseEventSpecificDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private String extractBlock(String md, String marker) {
        int idx = md.indexOf(marker);
        if (idx == -1) return null;
        int start = md.indexOf('{', idx);
        if (start == -1) return null;
        // find matching closing brace naive: find next '}' followed by newline
        int end = md.indexOf('\n}', start);
        if (end == -1) {
            // fallback to next blank line after start
            end = md.indexOf("\n\n", start);
            if (end == -1) end = md.length()-1;
        } else {
            end = md.indexOf('}', start);
        }
        String block = md.substring(start, end+1);
        return block;
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
