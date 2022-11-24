package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASEDATE, DURATION, MPA_RATING) VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql,
                    new String[]{"ID"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        int filmId = (Objects.requireNonNull(keyHolder.getKey())).intValue();
        film.setId(filmId);
        if (film.getGenres() != null) {
            List<Genre> genres = removeGenreDuplicate(film);
            genreStorage.addFilmGenreByFilmId(filmId, genres);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, MPA_RATING = ? " +
                "WHERE ID = ?;";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (film.getGenres() != null) {
            List<Genre> genres = removeGenreDuplicate(film);
            genreStorage.removeFilmGenreByFilmId(film.getId());
            genreStorage.addFilmGenreByFilmId(film.getId(), genres);
        }
        return getFilmById(film.getId());
    }

    private List<Genre> removeGenreDuplicate(Film film) {
        film.setGenres(film.getGenres().stream().distinct().collect(Collectors.toList()));
        return film.getGenres();
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM FILMS AS F " +
                "LEFT JOIN MPA AS M ON M.MPA_RATING_ID = F.MPA_RATING;";
        log.info("Запрошен список всех Film");
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "SELECT * FROM FILMS AS F " +
                "LEFT JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID " +
                "LEFT JOIN MPA M ON M.MPA_RATING_ID = F.MPA_RATING " +
                "LEFT JOIN GENRES G ON G.GENRE_ID = FG.GENRE_ID " +
                "WHERE F.ID = ?;";
        List<Film> buffer = jdbcTemplate.query(sql, this::mapRowToFilm, filmId);
        if (buffer.size() > 0) {
            log.info("Удалось найти Film с id={}", filmId);
            return buffer.get(0);
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
        String sql = "SELECT * " +
                "FROM FILMS f " +
                "JOIN MPA m ON (m.mpa_rating_id = f.mpa_rating) " +
                "LEFT JOIN " +
                "(SELECT FILM_ID, COUNT(user_id) as count " +
                "FROM LIKES " +
                "GROUP BY film_id) fl ON (fl.FILM_ID = f.id)" +
                "ORDER BY fl.count DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int idBuffer = rs.getInt("ID");
        String nameBuffer = rs.getString("NAME");
        String descriptionBuffer = rs.getString("DESCRIPTION");
        LocalDate releaseDateBuffer = LocalDate.parse(rs.getString("RELEASEDATE"));
        int durationBuffer = rs.getInt("DURATION");
        Mpa mpaBuffer = new Mpa(rs.getInt("MPA_RATING_ID"),
                rs.getString("MPA_NAME"));
        List<Genre> genresBuffer = genreStorage.getGenresByFilmId(idBuffer);
        Film bufferFilm = new Film();
        bufferFilm.setId(idBuffer);
        bufferFilm.setName(nameBuffer);
        bufferFilm.setDescription(descriptionBuffer);
        bufferFilm.setReleaseDate(releaseDateBuffer);
        bufferFilm.setDuration(durationBuffer);
        bufferFilm.setMpa(mpaBuffer);
        bufferFilm.setGenres(genresBuffer);
        return bufferFilm;
    }
}