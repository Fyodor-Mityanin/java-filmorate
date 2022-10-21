package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UsersRelationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
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
        if (!userStorage.containsId(id)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", id));
        }
        if (!userStorage.containsId(friendId)) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", friendId));
        }
        if (isFriends(id, friendId)) {
            throw new UsersRelationException("Вы уже друзья");
        }
        User user = userStorage.getUserById(id);
        user.getFriends().add(friendId);
        User friend = userStorage.getUserById(friendId);
        friend.getFriends().add(id);
    }

    public User getById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        return user;
    }

    public boolean isFriends(Long id, Long friendId) {
        return userStorage.getUserById(id).getFriends().contains(friendId)
                && userStorage.getUserById(friendId).getFriends().contains(id);
    }

    public void destroyFriendship(Long id, Long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", id));
        }
        if (friend == null) {
            throw new UserNotFoundException(String.format("Юзера с id %d не существует", friendId));
        }
        if (!isFriends(id, friendId)) {
            throw new UsersRelationException("Вы и так не друзья");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getAllFriends(long id) {
        User user = userStorage.getUserById(id);
        List<User> friends = new ArrayList<>();
        user.getFriends().forEach(friendId -> friends.add(userStorage.getUserById(friendId)));
        return friends;
    }

    public List<User> getAllCommonFriends(long id, long otherId) {
        if (!userStorage.containsId(id)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        if (!userStorage.containsId(otherId)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", otherId));
        }
        Set<Long> userFriendsIds = new HashSet<>(userStorage.getUserById(id).getFriends());
        Set<Long> otherUserFriendsIds = new HashSet<>(userStorage.getUserById(otherId).getFriends());
        userFriendsIds.retainAll(otherUserFriendsIds);
        List<User> commonFriends = new ArrayList<>();
        userFriendsIds.forEach(friendId -> commonFriends.add(userStorage.getUserById(friendId)));
        return commonFriends;
    }
}
