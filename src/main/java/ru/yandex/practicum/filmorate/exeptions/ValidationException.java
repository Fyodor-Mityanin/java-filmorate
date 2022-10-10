package ru.yandex.practicum.filmorate.exeptions;

public class ValidationException extends Exception{
    public ValidationException(String msg) {
        super(msg);
    }
}
