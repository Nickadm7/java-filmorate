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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ModelUserValidTest {
    @Autowired
    MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private User getValidUser() {
        return new User(
                1,
                "Test@test.mail",
                "LoginTest",
                "NameTest",
                LocalDate.of(2007, 12, 12)
        );
    }

    @DisplayName("Создание пользователя с верными полями")
    @Test
    public void createValidUser() throws Exception {
        User currentTestUser = getValidUser();
        MvcResult mvcResult = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestUser)))
                .andExpect(status().isOk())
                .andReturn();
        Assertions.assertEquals(200, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация e-mail не содержит @")
    @Test
    public void createdUserWrongMail() throws Exception {
        User currentTestUser = getValidUser();
        currentTestUser.setEmail("test");
        MvcResult mvcResult = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestUser)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация e-mail пустой")
    @Test
    public void createdUserWrongMailNull() throws Exception {
        User currentTestUser = getValidUser();
        currentTestUser.setEmail("");
        MvcResult mvcResult = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestUser)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация login содержит пробел")
    @Test
    public void createdUserWrongLoginContainBackspace() throws Exception {
        User currentTestUser = getValidUser();
        currentTestUser.setLogin("test test");
        MvcResult mvcResult = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestUser)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация login пустой")
    @Test
    public void createdUserWrongLoginNull() throws Exception {
        User currentTestUser = getValidUser();
        currentTestUser.setLogin("");
        MvcResult mvcResult = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestUser)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация пустое имя, используем логин")
    @Test
    public void createdUserNameIsNull() throws Exception {
        User currentTestUser = getValidUser();
        currentTestUser.setName("");
        MvcResult mvcResult = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestUser)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        Assertions.assertEquals(200, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }

    @DisplayName("Валидация дата рождения в будущем")
    @Test
    public void createdUserWrongBirthday() throws Exception {
        User currentTestUser = getValidUser();
        currentTestUser.setBirthday(LocalDate.of(9999, 1, 1));
        MvcResult mvcResult = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(currentTestUser)))
                .andExpect(status().is4xxClientError())
                .andReturn();
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus(), "Проверка статуса");
    }
}