package com.olegivanov.moneytransferservice.service;

import com.olegivanov.moneytransferservice.dto.ConfirmOperationRequestDTO;
import com.olegivanov.moneytransferservice.dto.Response200DTO;
import com.olegivanov.moneytransferservice.dto.TransferRequestDTO;
import com.olegivanov.moneytransferservice.exceptions.*;
import com.olegivanov.moneytransferservice.model.*;
import com.olegivanov.moneytransferservice.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.olegivanov.moneytransferservice.model.AuthorizationStatus.AUTHORIZED;


// * Выполняет операции по переводу денег:
// * - конвертацию запросов, от клиентского приложения в формат запросов требуемый IPSP провайдером,
// * - вызовы необходимых методов IPSP провайдер,
// * - возврат результатов контроллеру,
// * - запись истории в лог транзакций.

@Service
public class OperationsService {

    private final TransactionLog transactionLog;
    private final LogService logService;
    private final AcquiringService acquiringService;
    private Transaction transaction;

    public OperationsService(TransactionLog transactionLog, AcquiringService acquiringService, LogService logService) {
        this.transactionLog = transactionLog;
        this.acquiringService = acquiringService;
        this.logService = logService;
    }


//     * Инициирует обработку транзакции перевода денег
//     * @param reqDTO - DTO клиентского запроса на перевод
//     * @return - DTO успешно инициированной операции в случае успешной инициации

    public Response200DTO transfer(TransferRequestDTO reqDTO) {
        //("открываем") транзакцию обработки, пока без таймстемпа, id и со статусом fail
        transaction = new Transaction(reqDTO.getCardFromNumber(), reqDTO.getCardToNumber(),
                reqDTO.getAmount().getValue(), reqDTO.getAmount().getCurrency(), false);

        //записываем в лог транзакций
        transactionLog.put(transaction);

        //создаем из dto объект запроса в IPSP сервис
        TransactionAuthorizationRequest authReq = new TransactionAuthorizationRequest(
                reqDTO.getCardFromNumber(), reqDTO.getCardToNumber(), reqDTO.getCardFromCVV(),
                reqDTO.getCardFromValidTill(), BigDecimal.valueOf(reqDTO.getAmount().getValue()),
                reqDTO.getAmount().getCurrency());

        //запрашиваем IPSP провести транзакцию
        AuthorizationStatus authStatus = acquiringService.authorizeTransaction(authReq);

        //разбираем результат возвращенный сервисом IPSP:
        if (authStatus != AUTHORIZED) {
            //пишем в логфайл как failed
            logService.writeToLogFile(transaction);

            //разбираем какой эксепшон выкинуть - определяет, что отобразится клиенту
            switch (authStatus) {
                case UNAUTHORIZED: {
                    throw new UnauthorizedException("Отказ. Карта/карты не авторизованы.");
                }
                case INSUFFICIENT: {
                    throw new UnauthorizedException("Отказ. Недостаточно средств.");
                }
                case ERROR: {
                    throw new UnauthorizedException("Ошибка банковского сервиса.");
                }
                //не AUTHORIZED и неизвестный нам ответ IPSP
                default: throw new RuntimeException("Неизвестная ошибка сервиса.");
            }
        } else {
            //пока не верифицирован код подтверждения поэтому пока транзакция не выполнена и пока не успешна
            return new Response200DTO(Long.toString(transaction.getOperationId()), null);
        }
    }


//     * Завершает обработку транзакции перевода денег
//     * @param reqDTO - DTO запроса клиента на верификацию
//     * @return DTO успешно выполненной операции в случае успешного перевода

    public Response200DTO confirmOperation(ConfirmOperationRequestDTO reqDTO) {
        //проверяем результат верификации проверочного кода
        if (!acquiringService.verifyConfirmationCode(reqDTO.getOperationId(), reqDTO.getCode())) {
            //пишем ее в файл лога транзакций как failed
            logService.writeToLogFile(transaction);
            throw new UnauthorizedException("Неверный проверочный код.");
        }

        //успешно, отмечаем текущую транзакцию как успешную и пишем в логфайл
        transactionLog.update(transaction.getOperationId(), transaction.setSuccess());

        //пишем ее в файл лога транзакций как success
        logService.writeToLogFile(transaction);

        /* Предоставленный в условии задания FRONT, никак не отображает полученный проверочный код,
         * для выполнения требований спецификации OpenApi нашего сервиса, просто возвращаем,
         * тот который получили в реквесте. */
        return new Response200DTO(reqDTO.getOperationId(), reqDTO.getCode());
    }
}
