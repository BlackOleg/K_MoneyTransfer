package com.olegivanov.moneytransferservice.dto;

import lombok.Data;

@Data
public class ConfirmOperationRequestDTO {
     String operationId;
     String code;
}
