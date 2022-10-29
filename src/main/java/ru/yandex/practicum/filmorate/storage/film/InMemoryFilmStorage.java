package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> filmStorage = new HashMap<>();
    private Integer idFilmStorage = 1;

    public Film addFilm(Film film) {
        film.setId(createNewId());
        filmStorage.put(film.getId(), film);
        log.info("addFilm в filmStorage id {}, name {}", film.getId(), film.getName());
        return film;
    }

    public Film updateFilm(Film film) {
        if (filmStorage.containsKey(film.getId())) {
            filmStorage.put(film.getId(), film);
            log.info("updateFilm в filmStorage id {}, name {}", film.getId(), film.getName());
            return film;
        } else {
            throw new ValidationException("updateFilm не найден id фильма для обновления");
        }
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.values();
    }

    public Film getFilmById(int filmId) {
        if (filmStorage.containsKey(filmId)) {
            return filmStorage.get(filmId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void userLikeFilm(int id, int userId) {
        if (filmStorage.containsKey(id)) {
            filmStorage.get(id).getLike().add(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void userDeleteLikeFilm(int id, int userId) {
        if (filmStorage.containsKey(id)) {
            filmStorage.get(id).getLike().remove(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private Integer createNewId() {
        return idFilmStorage++;
    }
}