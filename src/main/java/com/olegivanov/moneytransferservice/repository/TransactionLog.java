package com.olegivanov.moneytransferservice.repository;

import com.olegivanov.moneytransferservice.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.nio.file.StandardOpenOption.*;

/**
 * Хранилище лога транзакций
 *
 */
@Repository
public class TransactionLog {
    //map для хранения лога транзакций в памяти
    private final Map<Long, Transaction> log = new ConcurrentHashMap<>();

    //генератор id для генерации идентификатора транзакции в мапе
    private final AtomicLong id = new AtomicLong();

    //файл для записи лога транзакций на диск читаем из свойств приложения
    @Value("${application.transaction-logfile.name}")
    private String transactionLogFile;

    //path для использования в writer
    private Path logFilePath;

    //инициализируем path после того как конструктор отработал
    @PostConstruct
    void init() {
        logFilePath = Paths.get(transactionLogFile);
    }

    /**
     * Записывает транзакцию в файл лога транзакций на диске в режиме добавления
     *
     * @param transaction - транзакция
     */
    public void writeToLogFile(Transaction transaction) {
        try {
            Files.write(logFilePath, transaction.toString().getBytes(), WRITE, CREATE, APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     /**
     * Записывает транзакцию в лог транзакций в памяти, при этом генерируется id транзакции
     *
     * @param transaction - транзакция
      */
    public void put(Transaction transaction) {
        long currentId = id.incrementAndGet();
        transaction.setOperationId(currentId);
        log.put(currentId,transaction);
    }

    /**
     * Получает транзакцию по id
     *
     * @param operationId - id транзакции
     * @return транзакция
     */
    public Transaction get(long operationId) {
        return log.get(operationId);
    }

    /**
     * Обновляет данные транзакции в логе транзакций в памяти
     *
     * @param operationId - id транзакции
     * @param transaction - транзакция
     */
    public void update(long operationId, Transaction transaction) {
        log.put(operationId, transaction);
    }
}
