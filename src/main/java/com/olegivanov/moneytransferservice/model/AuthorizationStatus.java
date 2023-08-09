package com.olegivanov.moneytransferservice.model;

/**
 * Класс для получения результата операции от IPSP сервиса
 *
 */
public enum AuthorizationStatus {
    AUTHORIZED(value = 1),
    UNAUTHORIZED(value = 2),
    INSUFFICIENT(value = 3),
    ERROR(value = 4);

    private static int value;

    AuthorizationStatus(int i) {

    }
}


