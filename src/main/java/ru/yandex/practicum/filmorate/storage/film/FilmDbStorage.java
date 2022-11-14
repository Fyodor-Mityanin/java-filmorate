package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO FILMS (NAME, MPA, RATE, DESCRIPTION, RELEASE_DATE, DURATION) VALUES (?, ?, ?, ?, ?, ?)";

        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setLong(2, film.getMpa().getId());
            ps.setInt(3, film.getRate());
            ps.setString(4, film.getDescription());
            ps.setDate(5, Date.valueOf(film.getReleaseDate()));
            ps.setInt(6, film.getDuration());
            return ps;
        }, gkh);

        long id = Objects.requireNonNull(gkh.getKey()).longValue();
        log.info("Фильм создан, id={}", id);
        Optional<Film> newFilm = getById(id);
        return newFilm.orElseThrow();
    }

    @Override
    public Film update(Film film) {
        log.info("Начало обновления фильма в таблице");
        int rowNum = jdbcTemplate.update(
                "UPDATE FILMS SET NAME=?, MPA=?, RATE=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=? WHERE ID=?",
                film.getName(),
                film.getMpa().getId(),
                film.getRate(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );
        log.info("{} строк обновлено", rowNum);
        return film;
    }

    @Override
    public List<Film> getAll() {
        log.info("Поиск всех фильма");
        String sql = "SELECT F.*, group_concat(FG.GENRE_ID separator ',') AS GENRE\n" +
                     "FROM FILMS F\n" +
                     "LEFT JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID\n" +
                     "GROUP BY F.ID";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public boolean containsId(Long id) {
        String sql = "SELECT * FROM FILMS WHERE id = ?";
        return !jdbcTemplate.query(sql, this::makeFilm, id).isEmpty();
    }

    @Override
    public Optional<Film> getById(Long id) {
        log.info("Поиск фильма по id={}", id);
        String sql = "SELECT F.*, group_concat(FG.GENRE_ID separator ',') AS GENRE\n" +
                     "FROM FILMS F\n" +
                     "LEFT JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID\n" +
                     "WHERE id = ?\n" +
                     "GROUP BY F.ID";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::makeFilm, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void decreaseRating(Long filmId) {

    }

    @Override
    public void increaseRating(Long filmId) {

    }


    private Film makeFilm(ResultSet rs, int row) throws SQLException {
        String genres;
        try {
            genres = rs.getString("GENRE");
        } catch (SQLException e) {
            genres = null;
        }
        List<Long> genreList = genres != null ? Arrays
                .stream(genres.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList()) : null;

        Mpa mpa = Mpa.builder()
                .id(rs.getLong("MPA"))
                .build();
        return Film.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .genre(genreList)
                .mpa(mpa)
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .build();
    }
}
