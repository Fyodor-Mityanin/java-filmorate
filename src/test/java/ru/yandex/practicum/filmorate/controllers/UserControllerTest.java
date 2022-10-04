package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.springframework.test.util.AssertionErrors.assertTrue;

class UserControllerTest {
    HttpClient httpClient = HttpClient.newHttpClient();

    private final URI URL = URI.create("http://localhost:8080/users");

    @Test
    void createFailLogin() throws IOException, InterruptedException {
        String body = """
                {
                      "login": "dolore ullamco",
                      "email": "yandex@mail.ru",
                      "birthday": "2446-08-20"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URL)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue("Не 400 или 500", response.statusCode() == 400 || response.statusCode() == 500);
    }

    @Test
    void createFailEmail() throws IOException, InterruptedException {
        String body = """
                {
                    "login": "dolore ullamco",
                    "name": "",
                    "email": "mail.ru",
                    "birthday": "1980-08-20"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URL)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue("Не 400 или 500", response.statusCode() == 400 || response.statusCode() == 500);
    }

    @Test
    void createFailBirthday() throws IOException, InterruptedException {
        String body = """
                {
                    "login": "dolore",
                    "name": "",
                    "email": "test@mail.ru",
                    "birthday": "2446-08-20"
                }
                """;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URL)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue("Не 400 или 500", response.statusCode() == 400 || response.statusCode() == 500);
    }
}