package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    @GetMapping
    public Collection<Film> getFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }
}