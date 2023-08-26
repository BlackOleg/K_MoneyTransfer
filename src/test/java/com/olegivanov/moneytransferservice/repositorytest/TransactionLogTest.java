package com.olegivanov.moneytransferservice.repositorytest;

import com.olegivanov.moneytransferservice.model.FileLog;
import com.olegivanov.moneytransferservice.model.Transaction;
import com.olegivanov.moneytransferservice.repository.TransactionLog;
import com.olegivanov.moneytransferservice.service.LogService;
import org.aspectj.lang.annotation.AfterReturning;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionLogTest {
    static final String LOG_FILE = "transactionlogtest.log";

    //given
    static TransactionLog transactionLog = new TransactionLog();
    static LogService logService = new LogService();
    static FileLog fileLog = new FileLog();

    @BeforeAll
    public static void initSuite() {
        ReflectionTestUtils.setField(fileLog, "logFilePath", Paths.get(LOG_FILE));
    }

    @Test
    @Order(1)
    @DisplayName("put & get test")
    void putAndGetTest() {
        //when
        transactionLog.put(new Transaction(
                1111_1111_1111_1111L, 2222_2222_2222_2222L, 1000, "RUR", false));
        Transaction tr = transactionLog.get(1L);
        //then
        Assertions.assertEquals(tr.getOperationId(), 1L);
        Assertions.assertEquals(tr.getFrom(), 1111_1111_1111_1111L);
        Assertions.assertEquals(tr.getTo(), 2222_2222_2222_2222L);
        //время транзакции не ранее 2 секунд до момента проверки
        Assertions.assertTrue(tr.getTimeStamp().isAfter(LocalDateTime.now().minusSeconds(2)));
        Assertions.assertEquals(tr.getCurrency(), "RUR");
        Assertions.assertEquals(tr.getValue(), 1000);
        Assertions.assertFalse(tr.isSuccess());
    }

    @Test
    @Order(2)
    @DisplayName("update test")
    void updateTest() {
        //when
        transactionLog.put(new Transaction(
                1111_1111_1111_1111L, 2222_2222_2222_2222L, 1000, "RUR", false));
        Transaction tr = transactionLog.get(2L).setSuccess();
        transactionLog.update(2L, tr);
        tr = transactionLog.get(2L);
        //then
        Assertions.assertEquals(tr.getOperationId(), 2L);
        Assertions.assertEquals(tr.getFrom(), 1111_1111_1111_1111L);
        Assertions.assertEquals(tr.getTo(), 2222_2222_2222_2222L);
        //время транзакции не ранее 2 секунд до момента проверки
        Assertions.assertTrue(tr.getTimeStamp().isAfter(LocalDateTime.now().minusSeconds(2)));
        Assertions.assertEquals(tr.getCurrency(), "RUR");
        Assertions.assertEquals(tr.getValue(), 1000);
        Assertions.assertTrue(tr.isSuccess());
    }

    @Test
    @Order(3)
    @DisplayName("write log to file test")
    void writeToLogFileTest() throws IOException {
        //when
        Transaction tr = transactionLog.get(2L);
        Files.deleteIfExists(Paths.get(LOG_FILE));
        logService.writeToLogFile(tr);
        String readStr = Files.readAllLines(Paths.get(LOG_FILE))
                .stream().reduce("", (a, b) -> a + b);
        //then
        Assertions.assertEquals(
                "2," + tr.getTimeStamp() +
                        ",1111-1111-1111-1111,2222-2222-2222-2222,1000.00,RUR,success",
                readStr);
        Files.deleteIfExists(Paths.get(LOG_FILE));
    }
}
