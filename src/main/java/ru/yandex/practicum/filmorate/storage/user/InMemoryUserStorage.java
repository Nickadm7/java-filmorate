package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userStorage = new HashMap<>();
    private Integer idUserStorage = 1;

    public User addUser(User user) {
        if (user.getFriends() == null || user.getFriends().isEmpty()) {
            user.setFriends(Collections.emptySet());
        }
        checkAndSetName(user);
        user.setId(createNewId());
        Set<Integer> friends = user.getFriends();
        for (Integer friend : friends) {
            if (!userStorage.containsKey(friend)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
        for (Integer friend : friends) {
            userStorage.get(friend).getFriends().add(user.getId());
        }
        userStorage.put(user.getId(), user);
        log.info("addUser в userStorage id {}, name {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        if (userStorage.containsKey(id) & userStorage.containsKey(friendId)) {
            if (userStorage.get(id).getFriends() != null & userStorage.get(friendId) != null) {
                userStorage.get(id).getFriends().add(friendId);
                userStorage.get(friendId).getFriends().add(id);
                System.out.println("Ветвление Ok");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if (id == friendId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (userStorage.containsKey(id) & userStorage.containsKey(friendId)) {
            if (userStorage.get(id) != null & userStorage.get(friendId) != null) {
                if (userStorage.get(id).getFriends().contains(friendId)) {
                    userStorage.get(id).getFriends().remove(friendId);
                    userStorage.get(friendId).getFriends().remove(id);
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
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

    public Set<Integer> getUserFriends(int id) {
        if (userStorage.containsKey(id)) {
            return userStorage.get(id).getFriends();
        } else {
            return null;
        }
    }

    @Override
    public Set<Integer> getCommonFriends(int id, int otherId) {
        if (userStorage.containsKey(id) & userStorage.containsKey(otherId)) {
            if (userStorage.get(id).getFriends() != null || userStorage.get(otherId).getFriends() != null) {
                Set<Integer> intersection = new HashSet<Integer>(userStorage.get(id).getFriends());
                Set<Integer> bufferSetOtherId = userStorage.get(otherId).getFriends();
                intersection.retainAll(bufferSetOtherId);
                return intersection;
            } else {
                return null;
            }
        } else {
            return null;
        }

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

    public User getUserById(int id) {
        if (userStorage.containsKey(id)) {
            return userStorage.get(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private Integer createNewId() {
        return idUserStorage++;
    }
}