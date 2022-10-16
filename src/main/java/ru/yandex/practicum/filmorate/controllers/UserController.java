package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UsersRelationException;
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
        if (!userService.isIdExist(user.getId())) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", user.getId()));
        }
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getOneById(@PathVariable long id) {
        if (!userService.isIdExist(id)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        return userService.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addToFriends(@PathVariable Long id, @PathVariable Long friendId) {
        if (!userService.isIdExist(id)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", id));
        }
        if (!userService.isIdExist(friendId)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", friendId));
        }
        if (userService.isFriends(id, friendId)) {
            throw new UsersRelationException("Вы уже друзья");
        }
        userService.makeFriendship(id, friendId);
        return userService.getById(friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable Long id, @PathVariable Long friendId) {
        if (!userService.isIdExist(id)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", id));
        }
        if (!userService.isIdExist(friendId)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", friendId));
        }
        if (!userService.isFriends(id, friendId)) {
            throw new UsersRelationException("Вы и так не друзья");
        }
        userService.destroyFriendship(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable long id) {
        if (!userService.isIdExist(id)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getAllCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        if (!userService.isIdExist(id)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        if (!userService.isIdExist(otherId)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", otherId));
        }
        return userService.getAllCommonFriends(id, otherId);
    }
}
