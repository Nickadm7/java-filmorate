package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findGenres() {
        String sql = "SELECT * FROM GENRES";
        log.info("Запрошен список всех Genres");
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(int idGenre) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, idGenre);
        if (userRows.next()) {
            log.info("Запрошен Genre по id{}", idGenre);
            return jdbcTemplate.query(sql, this::mapRowToGenre, idGenre).get(0);
        } else {
            log.info("Не найден Genre по id{}", idGenre);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
    }

    @Override
    public List<Genre> getGenresByFilmId(int filmId) {
        final String getGenresFilm = "SELECT * FROM GENRES " +
                "LEFT JOIN FILM_GENRE FG ON GENRES.GENRE_ID = FG.GENRE_ID " +
                "WHERE FG.FILM_ID = ?";
        return jdbcTemplate.query(getGenresFilm, this::mapRowToGenre, filmId);
    }

    @Override
    public void addFilmGenreByFilmId(int idFilm, List<Genre> genres) {
        final String addGenreFilm = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(addGenreFilm, idFilm, genre.getId());
        }
    }

    @Override
    public void removeFilmGenreByFilmId(int idFilm) {
        final String removeGenreFilmSql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";

        jdbcTemplate.update(removeGenreFilmSql, idFilm);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("GENRE_ID"),
                rs.getString("NAME")
        );
    }
}