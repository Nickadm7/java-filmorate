package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @GetMapping
    public Collection<User> getUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.updateUser(user);
    }
}