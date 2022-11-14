package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        log.info("Начало записи юзера в таблицу");
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, gkh);

        long id = Objects.requireNonNull(gkh.getKey()).longValue();
        log.info("Юзер создан, id={}", id);
        Optional<User> newUser = getUserById(id);
        return newUser.orElseThrow();
    }

    @Override
    public User update(User user) {
        log.info("Начало обновления юзера в таблице");
        String sql = "UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE ID=?";
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        int rowNum = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("{} строк обновлено", rowNum);
        return user;
    }

    @Override
    public List<User> getAll() {
        log.info("Поиск всех юзеров");
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public boolean containsId(long id) {
        return !jdbcTemplate.query("SELECT * FROM USERS WHERE id = ?", this::makeUser, id).isEmpty();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("Поиск юзера по id={}", id);
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?",this::makeUser,id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("ID"))
                .email(rs.getString("EMAIL"))
                .login(rs.getString("LOGIN"))
                .name(rs.getString("NAME"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}
