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
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
                .addValue("duration", film.getDuration());

        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId(id.intValue());
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
            return film;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM FILMS;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                LocalDate.parse(rs.getString("releaseDate")),
                rs.getInt("duration"))
        );
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "SELECT * FROM FILMS WHERE id = ?;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, filmId);
        if (filmRows.next()) {
            return new Film(
                    filmRows.getInt("id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    LocalDate.parse(Objects.requireNonNull(filmRows.getString("releaseDate"))),
                    filmRows.getInt("duration"));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }
    }

    @Override
    public void userLikeFilm(int id, int userId) {
        if (getFilmById(id) != null) {
            String sql = "MERGE INTO LIKES KEY(FILM_ID, USER_ID) VALUES (?, ?);";
            jdbcTemplate.update(sql, id, userId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("User id={} поставил лайк Film id={}", userId, id);
    }

    @Override
    public void userDeleteLikeFilm(int id, int userId) {
        String sql = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?;";
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet(sql, id, userId);
        if (likeRows.next()) {
            String sql1 = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
            jdbcTemplate.update(sql1, id, userId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        }
        log.info("userDeleteLikeFilm");
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }
}
