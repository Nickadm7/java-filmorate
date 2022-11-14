package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("users").usingGeneratedKeyColumns("id");
        SqlParameterSource parametersU = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("email", user.getEmail())
                .addValue("birthday", user.getBirthday());
        Number id = simpleJdbcInsert.executeAndReturnKey(parametersU);
        user.setId(id.intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (getUserById(user.getId()) != null) {
            String sql = "MERGE INTO users (id, login, email, name, birthday) KEY (id) VALUES (?, ?, ?, ?, ?);";
            jdbcTemplate.update(sql,
                    user.getId(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getName(),
                    user.getBirthday());
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("login"),
                rs.getString("email"),
                rs.getString("name"),
                LocalDate.parse(rs.getString("birthday")))
        );
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM USERS WHERE id = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (userRows.next()) {
            return new User(
                    userRows.getInt("id"),
                    userRows.getString("login"),
                    userRows.getString("email"),
                    userRows.getString("name"),
                    LocalDate.parse(Objects.requireNonNull(userRows.getString("birthday"))));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sql = "MERGE INTO FRIENDSHIP KEY(from_user_id, to_user_id) VALUES (?, ?, ?);";
        jdbcTemplate.update(sql, id, friendId, true);
    }

    @Override
    public void deleteFriend(int id, int friendId) {

    }

    @Override
    public Collection<User> getUserFriends(int id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        return null;
    }


}
