package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service("UserDbService")
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        log.info("Добавлен новый User");
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        log.info("Обновлен User");
        return userStorage.updateUser(user);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        log.info("Запрошен список общих друзей у User с id{} и id{}", id, otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    public Collection<User> getAllUsers() {
        log.info("Запрошен список всех User");
        return userStorage.getAllUsers();
    }

    public Collection<User> getUserFriends(int id) {
        log.info("Запрошен список всех Friend у User с id{}", id);
        return userStorage.getUserFriends(id);
    }

    public void addFriend(int id, int friendId) {
        log.info("Добавлен Friend с id{} к User с id{}", friendId, id);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        log.info("Удален Friend с id{} к User с id{}", friendId, id);
        userStorage.deleteFriend(id, friendId);
    }

    public User getUserById(int id) {
        log.info("Запрошен User с id{}", id);
        return userStorage.getUserById(id);
    }
}