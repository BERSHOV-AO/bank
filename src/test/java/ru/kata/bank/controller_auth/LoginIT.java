package ru.kata.bank.controller_auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import ru.kata.bank.ContextIT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/controller_auth/login.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/clear.sql")
public class LoginIT extends ContextIT {

    @Test
    @DisplayName("Успешная аутентификация пользователя")
    public void login_success() throws Exception {
        String login = "79090000001";
        String password = "password-1";
        mockMvc.perform(post("/api/bank/login")
                        .param("login", login)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("Пользователь с данный логином не найдет")
    public void login_failLogin() throws Exception {
        String invalidLogin = "79125677760";
        String password = "0000";
        mockMvc.perform(post("/api/bank/login")
                        .param("login", invalidLogin)
                        .param("password", password))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Пользователь с данным логином не найден"));
    }

    @Test
    @DisplayName("Пользователь ввел некорректный пароль")
    public void login_failPassword() throws Exception {
        String login = "79121567758";
        String invalidPassword = "4897439875983448";
        mockMvc.perform(post("/api/bank/login")
                        .param("login", login)
                        .param("password", invalidPassword))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Пользователь ввел некоректный пароль"));
    }
}
