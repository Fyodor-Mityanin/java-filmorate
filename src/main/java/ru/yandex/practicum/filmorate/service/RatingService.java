package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingStorage ratingStorage;

    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Rating> findAll() {
        return ratingStorage.getAll();
    }

    public Rating getById(long id) {
        Optional<Rating> rating = ratingStorage.getRatingById(id);
        return rating.orElseThrow(() -> new RatingNotFoundException(String.format("Рейтинг с id %d не найден", id)));
    }
}
