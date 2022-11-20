package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void addTest() {
        User user = createUser1();
        userStorage.add(user);
        Optional<User> userOptional = userStorage.getUserById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void update() {
        User user = createUser1();
        userStorage.add(user);
        User userUpdate = createUser2();
        userUpdate.setId(1L);
        userStorage.update(userUpdate);
        Optional<User> userOptional = userStorage.getUserById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                            assertThat(u).hasFieldOrPropertyWithValue("id", 1L);
                            assertThat(u).hasFieldOrPropertyWithValue("name", "USER2");
                        }
                );
    }

    @Test
    void getAll() {
        User user = createUser1();
        User user2 = createUser2();
        userStorage.add(user);
        userStorage.add(user2);
        Assertions.assertEquals(2, userStorage.getAll().size());
    }

    @Test
    void containsId() {
        User user = createUser1();
        userStorage.add(user);
        Assertions.assertTrue(userStorage.containsId(1L));
    }

    @Test
    void getUserById() {
        User user = createUser1();
        userStorage.add(user);
        Optional<User> userOptional = userStorage.getUserById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void createSubscribe() {
        User user = createUser1();
        User user2 = createUser2();
        userStorage.add(user);
        userStorage.add(user2);
        userStorage.createSubscribe(1L, 2L);
        Assertions.assertTrue(userStorage.isSubscribe(1L, 2L));
    }

    @Test
    void isSubscribe() {
        User user = createUser1();
        User user2 = createUser2();
        userStorage.add(user);
        userStorage.add(user2);
        userStorage.createSubscribe(1L, 2L);
        Assertions.assertTrue(userStorage.isSubscribe(1L, 2L));
    }

    @Test
    void removeSubscribe() {
        User user = createUser1();
        User user2 = createUser2();
        userStorage.add(user);
        userStorage.add(user2);
        userStorage.createSubscribe(1L, 2L);
        Assertions.assertTrue(userStorage.isSubscribe(1L, 2L));
    }

    @Test
    void getSubscribers() {
        User user = createUser1();
        User user2 = createUser2();
        userStorage.add(user);
        userStorage.add(user2);
        userStorage.createSubscribe(1L, 2L);
        assertEquals(1, userStorage.getSubscribers(1L).size());
    }

    private User createUser1() {
        return User.builder()
                .email("email@dsd.we")
                .login("LOGIN")
                .name("NAME")
                .birthday(LocalDate.parse("1988-05-05"))
                .build();
    }

    private User createUser2() {
        return User.builder()
                .email("ee2ed@dsd.we")
                .login("LOGIN2")
                .name("USER2")
                .birthday(LocalDate.parse("1922-05-05"))
                .build();
    }
}