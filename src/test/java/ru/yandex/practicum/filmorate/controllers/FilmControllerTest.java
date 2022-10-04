package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;


class FilmControllerTest {

    static FilmController filmController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void createFailDate() {
        Film film = Film.builder()
                .id(1)
                .name("Кинч 1")
                .description("Описание кинча")
                .releaseDate(LocalDate.of(555, 2, 3))
                .build();
        ValidationException thrown = Assertions.assertThrows(
                ValidationException.class,
                () -> filmController.create(film));
        assertTrue(thrown.getMessage().contains("Дата не валидна"));
    }
}