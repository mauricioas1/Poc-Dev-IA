package com.roadcard.dockwebhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseApprovedDto {
    @JsonProperty("purchase_id")
    @NotNull
    private Long purchaseId;

    @JsonProperty("amount")
    @NotNull
    private BigDecimal amount;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("transaction_uuid")
    private String transactionUuid;
}
