package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("films").usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa", film.getMpa())
                .addValue("genres", film.getGenres());
        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId(id.intValue());
        log.info("Добавлен Film с id={}", id.intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()) != null) {
            String sql = "MERGE INTO FILMS (id, name, description, releaseDate, duration) KEY (id) VALUES (?, ?, ?, ?, ?);";
            jdbcTemplate.update(sql,
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration());
            log.info("Обновлен Film с id={}", film.getId());
            return film;
        } else {
            log.info("Не удалось обновить Film с id={}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM FILMS;";
        log.info("Запрошен список всех Film");
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "SELECT * FROM FILMS WHERE id = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, filmId);
        if (filmRows.next()) {
            log.info("Удалось найти Film с id={}", filmId);
            return new Film(
                    filmRows.getInt("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    LocalDate.parse(Objects.requireNonNull(filmRows.getString("releaseDate"))),
                    filmRows.getInt("duration"));
        } else {
            log.info("Не удалось найти Film с id={}", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }
    }

    @Override
    public void userLikeFilm(int id, int userId) {
        if (getFilmById(id) != null) {
            String sql = "MERGE INTO LIKES KEY(FILM_ID, USER_ID) VALUES (?, ?);";
            jdbcTemplate.update(sql, id, userId);
            log.info("User id={} поставил лайк Film id={}", userId, id);
        } else {
            log.info("Не удалось поставить лайкUser id={} Film id={}", userId, id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void userDeleteLikeFilm(int id, int userId) {
        String sql = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?;";
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet(sql, id, userId);
        if (likeRows.next()) {
            String sql1 = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
            jdbcTemplate.update(sql1, id, userId);
            log.info("User id={} удалил лайк Film id={}", userId, id);
        } else {
            log.info("Не удалось удалить лайк User id={} Film id={}", userId, id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("id");
        Mpa mpa = new Mpa();
        String sqlGenre =
                "SELECT g.genre_id AS genre_id, g.name AS name FROM film_genre AS fg"
                        + " JOIN genres AS g ON fg.genre_id = g.genre_id"
                        + " WHERE film_id = ?";
        List<Genre> genres = jdbcTemplate.query(
                sqlGenre,
                (resultSet, num) -> new Genre(
                        resultSet.getInt("genre_id"),
                        resultSet.getString("name")
                ), filmId);
        String sqlMpa =
                "SELECT mr.MPA_RATING_ID AS MPA_RATING_ID, mr.NAME AS NAME FROM FILM_MPA_RATING AS fmr"
                        + " JOIN MPA_RATINGS AS mr ON fmr.MPA_RATING_ID = mr.MPA_RATING_ID"
                        + " WHERE film_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlMpa, filmId);
        if (mpaRows.next()) {
            mpa = new Mpa(
                    mpaRows.getInt("id"),
                    mpaRows.getString("name"));
        }
        return new Film(
                filmId,
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releasedate").toLocalDate(),
                rs.getInt("duration"),
                mpa,
                genres.isEmpty() ? null : new HashSet<>(genres)
        );
    }
}
