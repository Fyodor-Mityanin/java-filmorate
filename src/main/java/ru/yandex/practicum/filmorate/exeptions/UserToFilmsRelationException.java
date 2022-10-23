package ru.yandex.practicum.filmorate.exeptions;

public class UserToFilmsRelationException extends RuntimeException {
    public UserToFilmsRelationException(String message) {
        super(message);
    }
}
