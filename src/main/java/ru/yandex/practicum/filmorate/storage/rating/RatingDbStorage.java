package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getAll() {
        String sql = "SELECT * FROM RATING";
        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> Rating.builder().id(rs.getLong("ID")).name(rs.getString("NAME")).build()
        );
    }

    @Override
    public Optional<Rating> getRatingById(long id) {
        String sql = "SELECT * FROM RATING WHERE id = ?";
        return Optional.ofNullable(
            jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> Rating.builder()
                            .id(rs.getLong("ID"))
                            .name(rs.getString("NAME"))
                            .build(),
                    id
            )
        );
    }
}
