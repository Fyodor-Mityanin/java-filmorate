package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final static LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("получение всех фильмов");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        log.info("добавление фильма");
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new ValidationException("Дата не валидна");
        }
        return filmService.add(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) throws ValidationException {
        log.info("обновление фильма");
        if (film.getId() == 0) {
            throw new ValidationException("film без id");
        }
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getOneById(@PathVariable long id) {
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getMostPopular(count);
    }
}
