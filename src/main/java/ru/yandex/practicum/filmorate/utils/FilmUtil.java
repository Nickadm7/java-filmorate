package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilmUtil {
    private final Map<Integer, Film> filmStorage = new HashMap<>();
    Integer idFilmStorage = 1;

    public Film addFilm(Film film) {
        if (validation(film)) {
            film.setId(createNewId());
            filmStorage.put(film.getId(), film);
            log.info("addFilm в filmStorage id {}, name {}", film.getId(), film.getName());
            return film;
        }
        return null;
    }

    public Film updateFilm(Film film) {
        if (validation(film)) {
            if (film.getId() != null) {
                if (filmStorage.containsKey(film.getId())) {
                    filmStorage.put(film.getId(), film);
                    log.info("updateFilm в filmStorage id {}, name {}", film.getId(), film.getName());
                    return film;
                } else {
                    throw new ValidationException("updateFilm не найден id фильма для обновления");
                }
            } else {
                film.setId(createNewId());
                filmStorage.put(film.getId(), film);
                log.info("updateFilm добавлен Film в filmStorage id {}, name {}", film.getId(), film.getName());
                return film;
            }
        }
        return null;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.values();
    }

    private boolean validation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза — не раньше 28.12.1895");
            throw new ValidationException("Дата релиза — не раньше 28.12.1895");
        }
        return true;
    }

    private Integer createNewId() {
        return idFilmStorage++;
    }
}
