package com.olegivanov.moneytransferservice.model;

import lombok.Getter;

import java.math.BigDecimal;


 // Используется сервисом IPSP провайдера в качестве параметра запроса на выполнение операции

@Getter
public class TransactionAuthorizationRequest {
    long cardFromNumber;
    long cardToNumber;
    String cardFromCVV;
    String cardFromValidTill;
    BigDecimal amount;
    String currency;

    public TransactionAuthorizationRequest(long cardFromNumber, long cardToNumber, String cardFromCVV, String cardFromValidTill, BigDecimal amount, String currency) {
        this.cardFromNumber = cardFromNumber;
        this.cardToNumber = cardToNumber;
        this.cardFromCVV = cardFromCVV;
        this.cardFromValidTill = cardFromValidTill;
        this.amount = amount;
        this.currency = currency;
    }
}
