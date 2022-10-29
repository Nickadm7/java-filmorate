package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    @GetMapping()
    public Collection<Film> getFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }


    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    @GetMapping("/popular")
    public Stream<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return inMemoryFilmStorage.getPopularFilms(count);
    }


    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @PutMapping("{id}/like/{userId}")
    public void userLikeFilm(@PathVariable int id, @PathVariable int userId) {
        inMemoryFilmStorage.userLikeFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void userDeleteLikeFilm(@PathVariable int id, @PathVariable int userId) {
        inMemoryFilmStorage.userDeleteLikeFilm(id, userId);
    }
}