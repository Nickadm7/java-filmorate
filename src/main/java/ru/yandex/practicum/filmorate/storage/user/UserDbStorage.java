package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        checkAndSetName(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("USERS").usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("email", user.getEmail())
                .addValue("birthday", user.getBirthday());
        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
        user.setId(id.intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (getUserById(user.getId()) != null) {
            String sql = "MERGE INTO USERS (id, login, email, name, birthday) KEY (id) VALUES (?, ?, ?, ?, ?);";
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
        String sql = "SELECT * FROM USERS;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("login"),
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
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    LocalDate.parse(Objects.requireNonNull(userRows.getString("birthday"))));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
    }

    @Override
    public void addFriend(int id, int friendId) {
        if (getUserById(id) != null && getUserById(friendId) != null) {
            String sql = "INSERT INTO FRIENDSHIP (FROM_USER_ID, TO_USER_ID, STATUS) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, id, friendId, true);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if (getUserById(id) != null && getUserById(friendId) != null) {
            String sql = "DELETE FROM FRIENDSHIP WHERE FROM_USER_ID = ? AND TO_USER_ID = ?;";
            boolean isDelete = jdbcTemplate.update(sql, id, friendId) < 1;
            if (isDelete) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не возможно удалить");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Collection<User> getUserFriends(int id) {
        String sql = "select u.* " +
                "from FRIENDSHIP as f " +
                "join USERS as u on f.FROM_USER_ID = u.ID " +
                "where f.FROM_USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        String sql = "select u.* " +
                "from FRIENDSHIP as f1 " +
                "join FRIENDSHIP as f2 on f2.FROM_USER_ID = ? " +
                "and f2.TO_USER_ID = f1.TO_USER_ID " +
                "join users as u on f1.TO_USER_ID = u.id " +
                "where f1.FROM_USER_ID = ?";

        return jdbcTemplate.query(sql, this::mapRowToUser, id, otherId);
    }

    private void checkAndSetName(User user) {
        if (user.getName() == null) {
            log.info("checkAndSetName добавлен User без Name");
            user.setName(user.getLogin());
        } else if (user.getName().isBlank()) {
            log.info("checkAndSetName добавлен User isBlank Name");
            user.setName(user.getLogin());
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(Integer.parseInt(resultSet.getString("id")),
                resultSet.getString("email"),
                resultSet.getString("name"),
                resultSet.getString("login"),
                resultSet.getDate("birthday").toLocalDate());

    }
}
