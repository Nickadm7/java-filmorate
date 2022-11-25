package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreDbStorage genreStorage;

    @Autowired
    public GenreService(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        log.info("Запрошен список всех Genres");
        return genreStorage.findGenres();
    }

    public Genre getGenreById(int genreId) {
        log.info("Запрошен Genre с id{}", genreId);
        return genreStorage.getGenreById(genreId);
    }
}