package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    User getUserById(int id);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    Set<Integer> getUserFriends(int id);

    Set<Integer> getCommonFriends(int id, int otherId);
}
