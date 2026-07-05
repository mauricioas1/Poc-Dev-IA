package com.roadcard.dockwebhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseProcessedDto {
    @JsonProperty("purchase_id")
    @NotNull
    private Long purchaseId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("transaction_uuid")
    private String transactionUuid;
}
