package com.olegivanov.moneytransferservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;
@Getter
@Setter
@NoArgsConstructor
public class FileLog {
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


    // Записывает транзакцию в файл лога транзакций на диске в режиме добавления

    // @param transaction - транзакция

//    public void writeToLogFile(Transaction transaction) {
//        try {
//            Files.write(logFilePath, transaction.toString().getBytes(), WRITE, CREATE, APPEND);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
