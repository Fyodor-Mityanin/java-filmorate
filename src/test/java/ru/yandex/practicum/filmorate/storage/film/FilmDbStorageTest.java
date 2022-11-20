package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    @Test
    void addTest() {
        Film film = createFilm1();
        filmStorage.add(film);
        Optional<Film> filmOptional = filmStorage.getById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void updateTest() {
        Film film = createFilm1();
        filmStorage.add(film);
        Film filmUpdate = createFilm2();
        filmUpdate.setId(1L);
        filmStorage.update(filmUpdate);
        Optional<Film> filmOptional = filmStorage.getById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                            assertThat(f).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(f).hasFieldOrPropertyWithValue("name", "Терминатор 2");
                        }
                );
    }

    @Test
    void getAllTest() {
        Film film = createFilm1();
        Film film2 = createFilm2();
        filmStorage.add(film);
        filmStorage.add(film2);
        Assertions.assertEquals(2, filmStorage.getAll().size());
    }

    @Test
    void containsIdTest() {
        Film film = createFilm1();
        filmStorage.add(film);
        Assertions.assertTrue(filmStorage.containsId(1L));
    }

    @Test
    void getByIdTest() {
        Film film = createFilm1();
        filmStorage.add(film);
        Optional<Film> filmOptional = filmStorage.getById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void decreaseRatingTest() {
        Film film = createFilm1();
        filmStorage.add(film);
        filmStorage.decreaseRating(1L);
        Optional<Film> filmOptional = filmStorage.getById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("rate", -5)
                );
    }

    @Test
    void increaseRatingTest() {
        Film film = createFilm1();
        filmStorage.add(film);
        filmStorage.increaseRating(1L);
        Optional<Film> filmOptional = filmStorage.getById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("rate", 5)
                );
    }

    private Film createFilm1() {
        return Film.builder()
                .name("Терминатор")
                .mpa(mpaStorage.getMpaById(1L).orElseThrow())
                .description("Фильм про робота")
                .releaseDate(LocalDate.parse("1989-03-28"))
                .duration(120)
                .genres(Collections.singletonList(genreStorage.getGenreById(1L).orElseThrow()))
                .build();
    }

    private Film createFilm2() {
        return Film.builder()
                .name("Терминатор 2")
                .mpa(mpaStorage.getMpaById(2L).orElseThrow())
                .description("Хороший фильм про двух роботов")
                .releaseDate(LocalDate.parse("1992-03-28"))
                .duration(140)
                .genres(Collections.singletonList(genreStorage.getGenreById(2L).orElseThrow()))
                .build();
    }
}