package ru.yandex.practicum.filmorate.controllers;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

class FilmControllerTest {
    HttpClient httpClient = HttpClient.newHttpClient();
    final Gson gson = new Gson();
    private final String URL = "http://localhost:8080/films";

    @BeforeEach
    void setUp() {
//        Film film = new Film(1, "Кинч 1");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void create() {
//        Film film = new Film(1, "Кинч 1");
        URI url = URI.create(URL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
//                .POST()
                .build();



    }
}