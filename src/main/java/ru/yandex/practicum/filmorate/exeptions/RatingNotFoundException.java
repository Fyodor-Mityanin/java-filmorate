package ru.yandex.practicum.filmorate.exeptions;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}