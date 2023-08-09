package com.olegivanov.moneytransferservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Response200DTO {
    String operationId;
    @JsonInclude(JsonInclude.Include.NON_NULL) //если поле null, то оно не будет сериализовыватcя в Json
    String code;
}
