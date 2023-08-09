package com.olegivanov.moneytransferservice.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


import java.time.LocalDateTime;

import static com.olegivanov.moneytransferservice.utils.Utils.formatCardNumber;

@Getter
@NonNull
public class Transaction {
    //id операции
    @Setter
    private long operationId;

    //таймстэмп операции
    private final LocalDateTime timeStamp;

    //карта - источник
    private final long from;

    //карта - получатель
    private final long to;

    //сумма перевода
    private final int value;

    //Валюта перевода
    private final String currency;

    //Статус
    boolean success;

    public Transaction(long from, long to, int value, String currency, boolean success) {
        this.timeStamp = LocalDateTime.now();
        this.from = from;
        this.to = to;
        this.value = value;
        this.currency = currency;
        this.success = success;
    }

    public Transaction setSuccess() {
        success=true;
        return this;
    }

    public Transaction setFail() {
        success=false;
        return this;
    }

    @Override
    public String toString() {
        return operationId +
                "," + timeStamp +
                "," + formatCardNumber(from) +
                "," + formatCardNumber(to) +
                "," + value + ".00" +
                "," + currency +
                "," + (success? "success":"fail") +
                "\n";
    }
}
