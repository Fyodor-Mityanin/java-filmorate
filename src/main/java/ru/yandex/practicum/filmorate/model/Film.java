package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {

    private Long id;

    @NonNull
    @NotBlank
    private String name;

    @Size(max = 200, message = "Описание должно быть меньше 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive
    private int duration;

    private List<String> genre;

    private String rating;

    private final Set<Long> likes = new HashSet<>();
}
