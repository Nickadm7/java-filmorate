package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.stream.Stream;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(int filmId);

    void userLikeFilm(int id, int userId);

    void userDeleteLikeFilm(int id, int userId);

    Stream<Film> getPopularFilms(int count);
}