package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ModelFilmValidTest {
    @Autowired
    MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Film getValidFilm() {
        Set<Integer> likes = new HashSet<>();
        return new Film(
                1,
                "TestName",
                "Description",
                LocalDate.of(2007, 12, 12),
                100,
                likes

        );
    }

    @DisplayName("Создание фильма с верными полями")
    @Test
    public void createValidFilm() throws Exception {
        Film currentTestFilm = getValidFilm();
        MvcResult mvcResult = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestFilm)))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(200, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация фильма c пустым названием")
    @Test
    public void createFilmWrongName() throws Exception {
        Film currentTestFilm = getValidFilm();
        currentTestFilm.setName("");
        MvcResult mvcResult = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestFilm)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация фильма длина описания больше 200")
    @Test
    public void createFilmWrongDescription() throws Exception {
        Film currentTestFilm = getValidFilm();
        currentTestFilm.setDescription("asda".repeat(100));
        MvcResult mvcResult = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestFilm)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация фильма дата релиза неправильная")
    @Test
    public void createFilmWrongReleaseDate() throws Exception {
        Film currentTestFilm = getValidFilm();
        currentTestFilm.setReleaseDate(LocalDate.of(1111, 11, 11));
        MvcResult mvcResult = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestFilm)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация фильма duration отрицательная")
    @Test
    public void createFilmWrongDuration() throws Exception {
        Film currentTestFilm = getValidFilm();
        currentTestFilm.setDuration(-5);
        MvcResult mvcResult = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestFilm)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }
}