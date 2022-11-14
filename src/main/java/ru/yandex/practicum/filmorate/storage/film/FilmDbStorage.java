package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        return null;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return null;
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

    }

    @Override
    public void userDeleteLikeFilm(int id, int userId) {

    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }
}
