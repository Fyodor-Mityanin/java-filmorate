package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int idCounter = 1;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        film.setId(idCounter);
        film.setLikes(new HashSet<>());
        idCounter++;
        addToMap(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        addToMap(film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean containsId(long id) {
        return films.containsKey(id);
    }

    @Override
    public Film getById(long id) {
        return films.get(id);
    }

    private void addToMap(Film film) {
        films.put(film.getId(), film);
    }
}
