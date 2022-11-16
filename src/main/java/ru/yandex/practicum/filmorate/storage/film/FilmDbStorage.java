package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;
    private final HashMap<Long, Genre> genreCache = new HashMap<>();
    private final HashMap<Long, Mpa> mpaCache = new HashMap<>();

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaStorage, GenreDbStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @PostConstruct
    private void loadCache() {
        genreStorage.getAll().forEach(g -> genreCache.put(g.getId(), g));
        mpaStorage.getAll().forEach(m -> mpaCache.put(m.getId(), m));
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
        long filmId = Objects.requireNonNull(gkh.getKey()).longValue();
        if (film.getGenres() != null) {
            String sqlGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlGenre, filmId, genre.getId()));
        }
        film.setId(filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Начало обновления фильма в таблице");
        jdbcTemplate.update(
                "UPDATE FILMS SET NAME=?, MPA=?, RATE=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=? WHERE ID=?",
                film.getName(),
                film.getMpa().getId(),
                film.getRate(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );

        String sqlDelGenre = "DELETE FROM FILM_GENRE WHERE FILM_ID=?";
        jdbcTemplate.update(sqlDelGenre, film.getId());
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            String sqlInsGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            film.getGenres().forEach(
                    genre -> {
                        try {
                            jdbcTemplate.update(sqlInsGenre, film.getId(), genre.getId());
                        } catch (DuplicateKeyException e) {
                            log.info(e.getMessage());
                        }
                    }
            );

        }
        return getById(film.getId()).orElseThrow();
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
        String sql = "UPDATE FILMS SET RATE=RATE-5 WHERE id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void increaseRating(Long filmId) {
        String sql = "UPDATE FILMS SET RATE=RATE+5 WHERE id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Film makeFilm(ResultSet rs, int row) throws SQLException {
        List<Genre> genreList;
        try {
            String genres = rs.getString("GENRE");
            genreList = Arrays.stream(genres.split(","))
                    .map(id -> genreCache.get(Long.parseLong(id)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            genreList = Collections.emptyList();
        }
        return Film.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("NAME"))
                .genres(genreList)
                .mpa(mpaCache.get(rs.getLong("MPA")))
                .rate(rs.getInt("RATE"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .build();
    }
}
