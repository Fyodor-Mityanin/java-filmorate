package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaStorage;

import java.util.List;
import java.util.Optional;

@Service
public class MpaService {

    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> findAll() {
        return mpaStorage.getAll();
    }

    public Mpa getById(Long id) {
        Optional<Mpa> mpa = mpaStorage.getMpaById(id);
        return mpa.orElseThrow(() -> new RatingNotFoundException(String.format("Рейтинг с id %d не найден", id)));
    }
}
