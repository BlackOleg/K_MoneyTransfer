package com.olegivanov.moneytransferservice;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testcontainers.containers.GenericContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MoneyTransferServiceApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    static HttpHeaders headers = new HttpHeaders();


    //define container
    public static GenericContainer<?> restApp = new GenericContainer<>("rest-service-app")
            .withExposedPorts(5500);

    @BeforeAll
    public static void setUp() {
        //start containers
        restApp.start();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @Order(1)
    void contextLoadsTransferOk() {
        String jsonBodyStr = "{\n" +
                "  \"cardFromNumber\": \"1111222233334444\",\n" +
                "  \"cardToNumber\": \"4444333322221111\",\n" +
                "  \"cardFromCVV\": \"111\",\n" +
                "  \"cardFromValidTill\": \"01/23\",\n" +
                "  \"amount\": {\n" +
                "    \"currency\": \"RUR\",\n" +
                "    \"value\": 4000\n" +
                "  }\n" +
                "}";

        HttpEntity<String> request = new HttpEntity<>(jsonBodyStr, headers);
        String response = restTemplate.postForObject(
                "http://localhost:" + restApp.getMappedPort(5500) + "/transfer",request, String.class);
        assertEquals("{\"operationId\":\"1\"}", response);
    }

    @Test
    @Order(2)
    void contextLoadsTransferUnauthorized() {
        String jsonBodyStr = "{\n" +
                "  \"cardFromNumber\": \"9999999999999999\",\n" +
                "  \"cardToNumber\": \"4444333322221111\",\n" +
                "  \"cardFromCVV\": \"111\",\n" +
                "  \"cardFromValidTill\": \"01/23\",\n" +
                "  \"amount\": {\n" +
                "    \"currency\": \"RUR\",\n" +
                "    \"value\": 4000\n" +
                "  }\n" +
                "}";

        HttpEntity<String> request = new HttpEntity<>(jsonBodyStr, headers);
        String response = restTemplate.postForObject(
                "http://localhost:" + restApp.getMappedPort(5500) + "/transfer",request, String.class);
        assertEquals("{\"message\":\"Отказ. Карта/карты не авторизованы.\",\"id\":1}", response);
    }

    @Test
    @Order(3)
    void contextLoadsTransferInsufficient() {
        String jsonBodyStr = "{\n" +
                "  \"cardFromNumber\": \"1111222233334444\",\n" +
                "  \"cardToNumber\": \"4444333322221111\",\n" +
                "  \"cardFromCVV\": \"111\",\n" +
                "  \"cardFromValidTill\": \"01/23\",\n" +
                "  \"amount\": {\n" +
                "    \"currency\": \"RUR\",\n" +
                "    \"value\": 1000000\n" +
                "  }\n" +
                "}";

        HttpEntity<String> request = new HttpEntity<>(jsonBodyStr, headers);
        String response = restTemplate.postForObject(
                "http://localhost:" + restApp.getMappedPort(5500) + "/transfer",request, String.class);
        assertEquals("{\"message\":\"Отказ. Недостаточно средств.\",\"id\":1}", response);
    }

    @Test
    @Order(4)
    void contextLoadsTransferIPSPError() {
        String jsonBodyStr = "{\n" +
                "  \"cardFromNumber\": \"1111222233334444\",\n" +
                "  \"cardToNumber\": \"9999999999998888\",\n" +
                "  \"cardFromCVV\": \"111\",\n" +
                "  \"cardFromValidTill\": \"01/23\",\n" +
                "  \"amount\": {\n" +
                "    \"currency\": \"RUR\",\n" +
                "    \"value\": 1000\n" +
                "  }\n" +
                "}";

        HttpEntity<String> request = new HttpEntity<>(jsonBodyStr, headers);
        String response = restTemplate.postForObject(
                "http://localhost:" + restApp.getMappedPort(5500) + "/transfer",request, String.class);
        assertEquals("{\"message\":\"Ошибка банковского сервиса.\",\"id\":1}", response);
    }

    @Test
    @Order(5)
    void contextLoadsInternalServerError() {
        String jsonBodyStr = "1";

        HttpEntity<String> request = new HttpEntity<>(jsonBodyStr, headers);
        String response = restTemplate.postForObject(
                "http://localhost:" + restApp.getMappedPort(5500) + "/transfer",request, String.class);
        assertEquals("{\"message\":\"Внутренняя ошибка сервера\",\"id\":2}", response);
    }

    @Test
    @Order(6)
    void contextLoadsConfirmationOk() {
        String jsonBodyStr = "{\n" +
                "  \"operationId\": \"12345\",\n" +
                "  \"code\": \"12345code\"\n" +
                "}";

        HttpEntity<String> request = new HttpEntity<>(jsonBodyStr, headers);
        String response = restTemplate.postForObject(
                "http://localhost:" + restApp.getMappedPort(5500) + "/confirmOperation",request, String.class);
        assertEquals("{\"operationId\":\"12345\",\"code\":\"12345code\"}", response);
    }

    @Test
    @Order(6)
    void contextLoadsConfirmationError() {
        String jsonBodyStr = "{\n" +
                "  \"operationId\": \"12\",\n" +
                "  \"code\": \"12345code\"\n" +
                "}";

        HttpEntity<String> request = new HttpEntity<>(jsonBodyStr, headers);
        String response = restTemplate.postForObject(
                "http://localhost:" + restApp.getMappedPort(5500) + "/confirmOperation",request, String.class);
        assertEquals("{\"message\":\"Неверный проверочный код.\",\"id\":1}", response);
    }
}
