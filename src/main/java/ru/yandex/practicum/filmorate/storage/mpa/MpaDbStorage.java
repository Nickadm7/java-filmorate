package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("MpaDbStorage")
public class MpaDbStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findMpa() {
        String sql = "SELECT * FROM MPA_RATINGS";
        log.info("Запрошен список всех Genres");
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    @Override
    public Mpa getMpa(int mpaId) {
        String sql = "SELECT MPA_RATING_ID, NAME FROM MPA_RATINGS WHERE MPA_RATING_ID = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, mpaId);
        if (mpaRows.next()) {
            log.info("Запрошен Mpa по id={}", mpaId);
            return new Mpa(
                    mpaRows.getInt("MPA_RATING_ID"),
                    mpaRows.getString("NAME"));
        } else {
            log.info("Запрошен не существующий Mpa с id={}", mpaId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(
                rs.getInt("MPA_RATING_ID"),
                rs.getString("NAME")
        );
    }
}
