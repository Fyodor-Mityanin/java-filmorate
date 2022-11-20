package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return filmStorage.getAll();
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        if (!filmStorage.containsId(film.getId())) {
            throw new FilmNotFoundException(String.format("film c id %d не найден", film.getId()));
        }
        return filmStorage.update(film);
    }

    public Film getById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id %d не найден", id)));
    }

    public void addLike(Long filmId, Long userId) {
        if (!filmStorage.containsId(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
        if (!userStorage.containsId(userId)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", filmId));
        }
        filmStorage.increaseRating(filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.getById(filmId);
        if (film.isEmpty()) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
        if (!userStorage.containsId(userId)) {
            throw new UserNotFoundException(String.format("Юзер с id %d не найден", filmId));
        }
        filmStorage.decreaseRating(filmId);
    }

    public List<Film> getMostPopular(Integer count) {
        return filmStorage.getAll().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getRate(), o1.getRate()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
