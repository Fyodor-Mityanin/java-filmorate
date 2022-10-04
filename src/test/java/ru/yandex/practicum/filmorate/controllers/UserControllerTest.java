package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    static UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    @Test
    void putWithoutId() {
        User user = User.builder()
                .email("mail@mail.ru")
                .name("Юзер 1")
                .login("User luser")
                .build();
        ValidationException thrown = Assertions.assertThrows(
                ValidationException.class,
                () -> userController.put(user));
        assertTrue(thrown.getMessage().contains("User без id"));
    }
}