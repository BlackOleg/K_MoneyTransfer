package com.olegivanov.moneytransferservice.service;

import com.olegivanov.moneytransferservice.model.FileLog;
import com.olegivanov.moneytransferservice.model.Transaction;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.*;
@Service
public class LogService {
private  FileLog log;


    // Записывает транзакцию в файл лога транзакций на диске в режиме добавления

    // @param transaction - транзакция
    public void writeToLogFile(Transaction transaction) {
        try {
            Files.write(log.getLogFilePath(), transaction.toString().getBytes(), WRITE, CREATE, APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
