package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> findGenres();

    Genre getGenreById(int idGenre);

    List<Genre> getGenresByFilmId(int genreId);

    void addFilmGenreByFilmId(int idFilm, List<Genre> genres);

    void removeFilmGenreByFilmId(int idFilm);
}