package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UsersRelationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

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

    public void makeFriendship(Long id, Long friendId) {
        Optional<User> user = userStorage.getUserById(id);
        Optional<User> friend = userStorage.getUserById(friendId);
        if (isFriends(user, friend)) {
            throw new UsersRelationException("Вы уже друзья");
        }
        user.orElseThrow(() -> new UserNotFoundException(String.format("Юзера с id %d не существует", id)))
                .getFriends()
                .add(friendId);
        friend.orElseThrow(() -> new UserNotFoundException(String.format("Юзера с id %d не существует", id)))
                .getFriends()
                .add(id);
    }

    public User getById(Long id) {
        Optional<User> user = userStorage.getUserById(id);
        return user.orElseThrow(() -> new UserNotFoundException(String.format("Юзер с id %d не найден", id)));
    }

    public boolean isFriends(Optional<User> optUser, Optional<User> optFriend) {
        if (optUser.isEmpty() || optFriend.isEmpty()) {
            throw new UserNotFoundException("Юзера не существует");
        }
        User user = optUser.get();
        User friend = optFriend.get();
        return user.getFriends().contains(friend.getId()) && friend.getFriends().contains(user.getId());
    }

    public void destroyFriendship(Long id, Long friendId) {
        Optional<User> user = userStorage.getUserById(id);
        Optional<User> friend = userStorage.getUserById(friendId);
        if (!isFriends(user, friend)) {
            throw new UsersRelationException("Вы и так не друзья");
        }
        user.orElseThrow(() -> new UserNotFoundException(String.format("Юзера с id %d не существует", id)))
                .getFriends()
                .remove(friendId);
        friend.orElseThrow(() -> new UserNotFoundException(String.format("Юзера с id %d не существует", id)))
                .getFriends()
                .remove(id);
    }

    public List<User> getAllFriends(long id) {
        Optional<User> user = userStorage.getUserById(id);
        List<User> friends = new ArrayList<>();
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", id));
        }
        user.get().getFriends().forEach(
                friendId -> userStorage.getUserById(friendId).ifPresent(friends::add)
        );
        return friends;
    }

    public List<User> getAllCommonFriends(long id, long otherId) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        Optional<User> otherUser = userStorage.getUserById(otherId);
        if (otherUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", otherId));
        }
        Set<Long> userFriendsIds = new HashSet<>(user.get().getFriends());
        Set<Long> otherUserFriendsIds = new HashSet<>(otherUser.get().getFriends());
        userFriendsIds.retainAll(otherUserFriendsIds);
        List<User> commonFriends = new ArrayList<>();
        userFriendsIds.forEach(friendId -> userStorage.getUserById(friendId).ifPresent(commonFriends::add));
        return commonFriends;
    }

    public boolean isIdExist(long id) {
        return userStorage.containsId(id);
    }
}
