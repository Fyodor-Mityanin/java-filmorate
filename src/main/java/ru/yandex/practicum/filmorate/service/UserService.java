package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UsersRelationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage
    ) {
        this.userStorage = userStorage;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        if (!userStorage.containsId(user.getId())) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", user.getId()));
        }
        return userStorage.update(user);
    }

    public void makeSubscribe(Long authorId, Long subscriberId) {
        if (!userStorage.containsId(authorId) || !userStorage.containsId(subscriberId)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", subscriberId));
        }
        if (isSubscribe(authorId, subscriberId)) {
            throw new UsersRelationException("Вы уже подписаны на этого автора");
        }
        userStorage.createSubscribe(authorId, subscriberId);
    }

    public User getById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(String.format("Юзер с id %d не найден", id))
                );
    }

    public boolean isSubscribe(Long authorId, Long subscriberId) {
        return userStorage.isSubscribe(authorId, subscriberId);
    }

    public void removeSubscribe(Long authorId, Long subscriberId) {
        if (!userStorage.containsId(authorId) || !userStorage.containsId(subscriberId)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", subscriberId));
        }
        if (!isSubscribe(authorId, subscriberId)) {
            throw new UsersRelationException("Вы не подписаны на этого автора");
        }
        userStorage.removeSubscribe(authorId, subscriberId);
    }

    public List<User> getAllFriends(Long id) {
        if (!userStorage.containsId(id)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", id));
        }
        return userStorage.getSubscribers(id);
    }

    public List<User> getAllCommonFriends(Long id, Long otherId) {
        if (!userStorage.containsId(id) || !userStorage.containsId(otherId)) {
            throw new UserNotFoundException(String.format("Юзера с id %d или %d не существует", id, otherId));
        }
        Set<Long> userFriendsIds = userStorage.getSubscribers(id).stream().map(User::getId).collect(Collectors.toSet());
        Set<Long> otherUserFriendsIds = userStorage.getSubscribers(otherId).stream().map(User::getId).collect(Collectors.toSet());
        userFriendsIds.retainAll(otherUserFriendsIds);
        List<User> commonFriends = new ArrayList<>();
        userFriendsIds.forEach(friendId -> userStorage.getUserById(friendId).ifPresent(commonFriends::add));
        return commonFriends;
    }

    public boolean isIdExist(long id) {
        return userStorage.containsId(id);
    }
}
