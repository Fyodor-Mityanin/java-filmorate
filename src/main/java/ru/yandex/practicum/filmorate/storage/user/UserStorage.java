package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    User update(User user);

    List<User> getAll();

    boolean containsId(Long id);

    Optional<User> getUserById(Long id);

    void createSubscribe(Long authorId, Long subscriberId);

    boolean isSubscribe(Long authorId, Long subscriberId);

    void removeSubscribe(Long authorId, Long subscriberId);

    List<User> getSubscribers(Long id);
}
