package com.olegivanov.moneytransferservice.controller;

import com.olegivanov.moneytransferservice.dto.ConfirmOperationRequestDTO;
import com.olegivanov.moneytransferservice.dto.Response200DTO;
import com.olegivanov.moneytransferservice.dto.ResponseErrDTO;
import com.olegivanov.moneytransferservice.dto.TransferRequestDTO;
import com.olegivanov.moneytransferservice.exceptions.UnauthorizedException;

import com.olegivanov.moneytransferservice.service.OperationsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin //CORS on: фронт, загруженный из одного источника сможет обращаться к приложению, запущенному на другом
public class OperationsController {
    private final OperationsService service;

    public OperationsController(OperationsService service) {
        this.service = service;
    }

    @PostMapping(value = "/transfer")
        public Response200DTO transfer(@RequestBody TransferRequestDTO reqDto) {
        return service.transfer(reqDto);
    }

    @PostMapping(value = "/confirmOperation")
    public Response200DTO confirmOperation(@RequestBody ConfirmOperationRequestDTO reqDto) {
        return service.confirmOperation(reqDto);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnauthorizedException.class)
    ResponseErrDTO handleUnauthorized(UnauthorizedException e) {
        return new ResponseErrDTO(e.getMessage(), 1);
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    ResponseErrDTO handleInternalServerError(RuntimeException e) {
        return new ResponseErrDTO("Внутренняя ошибка сервера", 2);
    }
}
