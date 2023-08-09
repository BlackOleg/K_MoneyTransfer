package com.olegivanov.moneytransferservice.servicetest;

import com.olegivanov.moneytransferservice.dto.Response200DTO;
import com.olegivanov.moneytransferservice.dto.TransferRequestDTO;
import com.olegivanov.moneytransferservice.exceptions.UnauthorizedException;
import com.olegivanov.moneytransferservice.model.*;
import com.olegivanov.moneytransferservice.repository.TransactionLog;
import com.olegivanov.moneytransferservice.service.AcquiringService;
import com.olegivanov.moneytransferservice.service.OperationsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class OperationsServiceTest {
    static TransferRequestDTO reqDTO;
    static TransactionLog transactionLog;

    @BeforeAll
    public static void initSuite() {
        reqDTO = new TransferRequestDTO();
        //для теста не важна работа метода transactionLog.writeToLogFile, поэтому его заглушим
        transactionLog = Mockito.spy(TransactionLog.class);
        doNothing().when(transactionLog).writeToLogFile(isA(Transaction.class));

        //входной параметр для тестируемого метода
        reqDTO.setCardFromNumber(1111_1111_1111_1111L);
        reqDTO.setCardToNumber(1111_1111_1111_1111L);
        reqDTO.setCardFromCVV("111");
        TransferRequestDTO.AmountDto amountDto = new TransferRequestDTO.AmountDto();
        amountDto.setValue(1000);
        amountDto.setCurrency("RUR");
        reqDTO.setAmount(amountDto);
        reqDTO.getAmount().setCurrency("RUR");
    }

    /**
     * Тест метода transfer при успешном выполнении перевода
     *
     */
    @Test
    @DisplayName("Authorized transfer test")
    void transferTest() {
        //given:
        //mock для ipsp
        AcquiringService ipsp = mock(AcquiringService.class);

        when(ipsp.authorizeTransaction(isA(TransactionAuthorizationRequest.class))).
        thenReturn(AuthorizationStatus.AUTHORIZED);

        when(ipsp.verifyConfirmationCode(isA(String.class),isA(String.class))).thenReturn(true);

        //экземпляр сервиса
        OperationsService service = new OperationsService(transactionLog, ipsp);

        //when:
        Response200DTO respDTO = service.transfer(reqDTO);

        //then:
        assertEquals("1",respDTO.getOperationId());
    }

    /**
     * Тест метода transfer при отказе авторизации сервисом ipsp
     *
     */
    @Test
    @DisplayName("Unauthorized transfer test")
    void transferUnauthorizedTest(){
        //given:
        //mock для ipsp
        AcquiringService ipsp = mock(AcquiringService.class);

        when(ipsp.authorizeTransaction(isA(TransactionAuthorizationRequest.class))).
                thenReturn(AuthorizationStatus.UNAUTHORIZED);

        when(ipsp.verifyConfirmationCode(isA(String.class),isA(String.class))).thenReturn(true);

        //экземпляр сервиса
        OperationsService service = new OperationsService(transactionLog, ipsp);

        //when:
        UnauthorizedException ex = assertThrows(UnauthorizedException.class,()->service.transfer(reqDTO));

        //then:
        assertEquals("Отказ. Карта/карты не авторизованы.", ex.getMessage());
    }
}
