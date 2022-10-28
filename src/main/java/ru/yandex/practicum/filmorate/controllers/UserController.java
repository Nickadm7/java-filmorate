package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @GetMapping
    public Collection<User> getUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Получен запрос на получение фильма с id: {}", id);
        return inMemoryUserStorage.getUserById(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        inMemoryUserStorage.addFriend(id, friendId);
    }
}