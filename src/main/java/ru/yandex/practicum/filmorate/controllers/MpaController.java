package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable("id") int mpaId) {
        return mpaService.getMpa(mpaId);
    }
}