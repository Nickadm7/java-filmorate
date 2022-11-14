package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
        private final Integer fromUser; // Пользователь, предложивший дружбу
        private final Integer toUser;   // Пользователь, которому предложили дружбу
        private final boolean isMutual;
}

