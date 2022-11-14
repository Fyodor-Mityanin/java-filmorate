package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        log.info("Начало записи фильма в таблицу");
        int rowNum = jdbcTemplate.update(
                "INSERT INTO FILMS (NAME, RATING, DESCRIPTION, RELEASE_DATE, DURATION) VALUES (?, ?, ?, ?, ?)",
                film.getName(),
                film.getRating(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );

        log.info("{} строк записано", rowNum);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Начало обновления фильма в таблице");
        int rowNum = jdbcTemplate.update(
                "UPDATE FILMS SET NAME=?, RATING=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=? WHERE ID=?",
                film.getName(),
                film.getRating(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
        log.info("{} строк обновлено", rowNum);
        return film;
    }

    @Override
    public List<Film> getAll() {
        log.info("Поиск всех фильма");
        String sql = "SELECT F.*, group_concat(FG.GENRE_ID separator ',') AS GENRE\n" +
                "FROM FILMS F\n" +
                "JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID\n" +
                "GROUP BY F.ID";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public boolean containsId(long id) {
        String sql = "SELECT * FROM FILMS WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Film.class, id) != null;
    }

    @Override
    public Optional<Film> getById(long id) {
        log.info("Поиск фильма по id={}", id);
        String sql = "SELECT F.*, group_concat(FG.GENRE_ID separator ',') AS GENRE\n" +
                "FROM FILMS F\n" +
                "JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID\n" +
                "WHERE id = ?\n" +
                "GROUP BY F.ID";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeFilm, id));
    }


    private Film makeFilm(ResultSet rs, int row) throws SQLException {
        return Film.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .genre(List.of(rs.getString("GENRE").split(",")))
                .rating(rs.getString("RATING"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .build();
    }
}
