package com.roadcard.dockwebhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseEventDto {

    @JsonProperty("purchase_id")
    @NotNull
    private Long purchaseId;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("card_id")
    private Long cardId;

    @JsonProperty("product_id")
    private Integer productId;

    @JsonProperty("transaction_type_id")
    private String transactionTypeId;

    @JsonProperty("transaction_type_description")
    private String transactionTypeDescription;

    @JsonProperty("pan")
    private String pan;

    @JsonProperty("purchase_date")
    private String purchaseDate;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("source_amount")
    private BigDecimal sourceAmount;

    @JsonProperty("settlement_amount")
    private BigDecimal settlementAmount;

    @JsonProperty("installment")
    private Integer installment;

    @JsonProperty("authorization_code")
    private String authorizationCode;

    @JsonProperty("merchant")
    private String merchant;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("mcc")
    private Integer mcc;

    @JsonProperty("entry_mode")
    private String entryMode;

    @JsonProperty("nsu")
    private Long nsu;

    @JsonProperty("authorization_date")
    private String authorizationDate;

    @JsonProperty("transaction_identification")
    private Long transactionIdentification;

    @JsonProperty("status")
    private String status;

    @JsonProperty("status_id")
    private Integer statusId;

    @JsonProperty("currency_code")
    private Integer currencyCode;

    @JsonProperty("exchange_rate")
    private BigDecimal exchangeRate;

    @JsonProperty("transaction_uuid")
    private String transactionUuid;

    @JsonProperty("merchant_code")
    private String merchantCode;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("terminal")
    private String terminal;

    @JsonProperty("incoming_id")
    private String incomingId;

    @JsonProperty("acquiring_code")
    private Integer acquiringCode;

    @JsonProperty("properties")
    private Map<String, Object> properties;
}
