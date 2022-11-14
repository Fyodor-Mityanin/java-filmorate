package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM MPA";
        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> Mpa.builder().id(rs.getLong("ID")).name(rs.getString("NAME")).build()
        );
    }

    @Override
    public Optional<Mpa> getMpaById(long id) {
        String sql = "SELECT * FROM MPA WHERE id = ?";
        return Optional.ofNullable(
            jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> Mpa.builder()
                            .id(rs.getLong("ID"))
                            .name(rs.getString("NAME"))
                            .build(),
                    id
            )
        );
    }
}
