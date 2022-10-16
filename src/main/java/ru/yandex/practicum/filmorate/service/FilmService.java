package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public boolean isIdExist(long id) {
        return filmStorage.containsId(id);
    }

    public Film update(Film film) {
        Set<Long> likes = filmStorage.getById(film.getId()).getLikes();
        film.setLikes(likes);
        return filmStorage.update(film);
    }

    public Film getById(long id) {
        return filmStorage.getById(id);
    }

    public boolean isLiked(long id, long userId) {
        return filmStorage.getById(id).getLikes().contains(userId);
    }

    public void addLike(long id, long userId) {
        filmStorage.getById(id).getLikes().add(userId);
    }

    public void removeLike(long id, long userId) {
        filmStorage.getById(id).getLikes().remove(userId);
    }

    public List<Film> getMostPopular(Integer count) {
        return filmStorage.getAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
