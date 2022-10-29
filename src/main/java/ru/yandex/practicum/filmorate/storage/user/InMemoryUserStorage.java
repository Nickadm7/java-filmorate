package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component ("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userStorage = new HashMap<>();
    private Integer idUserStorage = 1;

    public User addUser(User user) {
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
            if (userStorage.get(id).getFriends().contains(friendId) || userStorage.get(friendId).getFriends().contains(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            userStorage.get(id).getFriends().add(friendId);
            userStorage.get(friendId).getFriends().add(id);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Collection<User> getAllUsers() {
        return userStorage.values();
    }

    public List<User> getUserFriends(int id) {
        List<User> friends = new ArrayList<>();
        if (userStorage.containsKey(id)) {
            if (!userStorage.get(id).getFriends().isEmpty()) {
                for (int currentid : userStorage.get(id).getFriends()) {
                    friends.add(userStorage.get(currentid));
                }
                return friends;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<User> friends = new ArrayList<>();
        if (userStorage.containsKey(id) && userStorage.containsKey(otherId)) {
            if (userStorage.get(id).getFriends() != null || userStorage.get(otherId).getFriends() != null) {
                Set<Integer> intersection = new HashSet<>(userStorage.get(id).getFriends());
                Set<Integer> bufferSetOtherId = userStorage.get(otherId).getFriends();
                intersection.retainAll(bufferSetOtherId);
                for (Integer currentIdCommon: intersection) {
                    friends.add(userStorage.get(currentIdCommon));
                }
                return friends;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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