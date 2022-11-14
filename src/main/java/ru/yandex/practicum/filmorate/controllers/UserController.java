package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("получение списка всех пользователей");
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("создание пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) throws ValidationException {
        log.info("обновление пользователя");
        if (user.getId() == 0) {
            throw new ValidationException("User без id");
        }
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getOneById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PutMapping("/{authorId}/friends/{subscriberId}")
    public void subscribe(@PathVariable Long authorId, @PathVariable Long subscriberId) {
        userService.makeSubscribe(authorId, subscriberId);
    }

    @DeleteMapping("/{authorId}/friends/{subscriberId}")
    public void unSubscribe(@PathVariable Long authorId, @PathVariable Long subscriberId) {
        userService.removeSubscribe(authorId, subscriberId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) {
        if (!userService.isIdExist(id)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getAllCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getAllCommonFriends(id, otherId);
    }
}
