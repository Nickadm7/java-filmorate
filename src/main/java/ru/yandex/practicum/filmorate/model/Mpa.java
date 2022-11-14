package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {
    private int id;
    private String name;

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
