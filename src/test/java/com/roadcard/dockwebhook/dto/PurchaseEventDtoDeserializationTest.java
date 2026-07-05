package com.roadcard.dockwebhook.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PurchaseEventDtoDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializePurchaseApprovedExample() throws Exception {
        String json = Files.readString(Path.of("Prompt.md"));
        // extract first JSON block for Purchase_approved from Prompt.md (simple approach)
        int start = json.indexOf("- Purchase_approved");
        int idx = json.indexOf("{" , start);
        int end = json.indexOf("}\n\n- Purchase_processed", idx);
        if (end == -1) end = json.indexOf('\n', idx);
        String block = json.substring(idx, end+1);

        PurchaseEventDto dto = mapper.readValue(block, PurchaseEventDto.class);
        assertEquals(151243393L, dto.getPurchaseId());
    }
}
