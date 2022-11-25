package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service("FilmDbService")
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getAllFilms() {
        log.info("Получен список всех фильмов");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        log.info("Запрошен Film по id{}", id);
        return filmStorage.getFilmById(id);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрошен спикок популярных Film в количестве{}", count);
        return filmStorage.getPopularFilms(count);
    }

    public Film addFilm(Film film) {
        log.info("Добавлен новый Film");
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновлен Film");
        return filmStorage.updateFilm(film);
    }

    public void userLikeFilm(int id, int userId) {
        log.info("Пользователь с id{} поставил лайк фильму с id{}", userId, id);
        filmStorage.userLikeFilm(id, userId);
    }

    public void userDeleteLikeFilm(int id, int userId) {
        log.info("Пользователь с id{} удалил лайк фильму с id{}", userId, id);
        filmStorage.userDeleteLikeFilm(id, userId);
    }
}