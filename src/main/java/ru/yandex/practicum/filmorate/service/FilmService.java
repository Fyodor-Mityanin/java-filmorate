package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UserToFilmsRelationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
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
        if (!isIdExist(film.getId())) {
            throw new FilmNotFoundException(String.format("film c id %d не найден", film.getId()));
        }
        return filmStorage.update(film);
    }

    public Film getById(long id) {
        if (!isIdExist(id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
        return filmStorage.getById(id);
    }

    public boolean isLiked(long id, long userId) {
        return filmStorage.getById(id).getLikes().contains(userId);
    }

    public void addLike(long id, long userId) {
        if (!isIdExist(id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
        if (!isIdExist(userId)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        if (isLiked(id, userId)) {
            throw new UserToFilmsRelationException("Фильм уже лайкнут");
        }
        filmStorage.getById(id).getLikes().add(userId);
    }

    public void removeLike(long id, long userId) {
        if (!isIdExist(id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
        if (!isIdExist(userId)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", id));
        }
        if (!isLiked(id, userId)) {
            throw new UserToFilmsRelationException("Фильм не лайкнут");
        }
        filmStorage.getById(id).getLikes().remove(userId);
    }

    public List<Film> getMostPopular(Integer count) {
        return filmStorage.getAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
