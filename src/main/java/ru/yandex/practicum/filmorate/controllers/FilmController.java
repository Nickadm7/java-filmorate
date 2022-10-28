package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    @GetMapping()
    public Collection<Film> getFilms() {
        log.info("Получен запрос на получение списка всех фильмов");
        return inMemoryFilmStorage.getAllFilms();
    }


    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("Получен запрос на получение фильма с id: {}", id);
        return inMemoryFilmStorage.getFilmById(id);
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма");
        return inMemoryFilmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }
}