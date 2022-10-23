package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private int idCounter = 1;

    @Override
    public User add(User user) {
        user.setId(idCounter);
        idCounter++;
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        addToMap(user);
        return user;
    }

    @Override
    public User update(User user) {
        addToMap(user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean containsId(long id) {
        return users.containsKey(id);
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    private void addToMap(User user) {
        users.put(user.getId(), user);
    }
}
