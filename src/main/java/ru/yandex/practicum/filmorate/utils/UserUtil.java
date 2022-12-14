package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserUtil {
    private final Map<Integer, User> userStorage = new HashMap<>();
    private Integer idUserStorage = 1;

    public User addUser(User user) {
        checkAndSetName(user);
        user.setId(createNewId());
        userStorage.put(user.getId(), user);
        log.info("addUser в userStorage id {}, name {}", user.getId(), user.getName());
        return user;
    }

    public User updateUser(User user) {
        if (userStorage.containsKey(user.getId())) {
            userStorage.put(user.getId(), user);
            log.info("updateUser в userStorage id {}, name {}", user.getId(), user.getName());
            return user;
        } else {
            throw new ValidationException("updateUser не найден id user для обновления");
        }
    }

    public Collection<User> getAllUsers() {
        return userStorage.values();
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

    private Integer createNewId() {
        return idUserStorage++;
    }
}