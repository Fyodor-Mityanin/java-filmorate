package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.springframework.test.util.AssertionErrors.assertTrue;

class FilmControllerTest {
    HttpClient httpClient = HttpClient.newHttpClient();

    private final URI URL = URI.create("http://localhost:8080/films");

    @Test
    void createFailName() throws IOException, InterruptedException {
        String body = "{\n" +
                "    \"name\": \"\",\n" +
                "    \"description\": \"Description\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "    \"duration\": 200\n" +
                "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URL)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue("Не 400 или 500", response.statusCode() == 400 || response.statusCode() == 500);
    }

    @Test
    void createFailDescription() throws IOException, InterruptedException {
        String body = "{\n" +
                "  \"name\": \"Film name\",\n" +
                "  \"description\": \"Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URL)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue("Не 400 или 500", response.statusCode() == 400 || response.statusCode() == 500);
    }

    @Test
    void createFailReleaseDate() throws IOException, InterruptedException {
        String body = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Description\",\n" +
                "  \"releaseDate\": \"1890-03-25\",\n" +
                "  \"duration\": 200\n" +
                "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URL)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue("Не 400 или 500", response.statusCode() == 400 || response.statusCode() == 500);
    }

    @Test
    void createFailDuration() throws IOException, InterruptedException {
        String body = "{\n" +
                "  \"name\": \"Name\",\n" +
                "  \"description\": \"Descrition\",\n" +
                "  \"releaseDate\": \"1980-03-25\",\n" +
                "  \"duration\": -200\n" +
                "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URL)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertTrue("Не 400 или 500", response.statusCode() == 400 || response.statusCode() == 500);
    }
}