package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private Long idCounter = 1L;

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
    public boolean containsId(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void createSubscribe(Long subscriberId, Long authorId) {
        users.get(authorId).getSubscribers().add(subscriberId);
    }

    @Override
    public boolean isSubscribe(Long authorId, Long subscriberId) {
        return users.get(authorId).getSubscribers().contains(subscriberId);
    }

    @Override
    public void removeSubscribe(Long authorId, Long subscriberId) {
        users.get(authorId).getSubscribers().remove(subscriberId);
    }

    @Override
    public List<User> getSubscribers(Long id) {
        List<User> subscribers = new ArrayList<>();
        users.get(id).getSubscribers().forEach(subId -> subscribers.add(users.get(subId)));
        return subscribers;
    }

    private void addToMap(User user) {
        users.put(user.getId(), user);
    }
}
