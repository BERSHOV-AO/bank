package ru.kata.bank.controller_internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import ru.kata.bank.ContextIT;
import ru.kata.bank.model.dto.LoginRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/controller_auth/login.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/clear.sql")
public class LoginIT extends ContextIT {

    private static final String LOGIN = "79090000001";
    private static final String PASSWORD = "password-1";
    private static final String ENDPOINT_URL = "/api/bank/login";

    @Test
    @DisplayName("Успешная аутентификация пользователя")
    public void login_success() throws Exception {
        performRequest(LOGIN, PASSWORD)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("Пользователь с данный логином не найдет")
    public void login_failLogin() throws Exception {
        String invalidLogin = "79125677760";
        String errorMsg = String.format("Активный пользователь MIS с логином %s не найден", invalidLogin);
        performRequest(invalidLogin, PASSWORD)
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(errorMsg));
    }

    @Test
    @DisplayName("Пользователь ввел некорректный пароль")
    public void login_failPassword() throws Exception {
        performRequest(LOGIN, "4897439")
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Пароль не совпадает"));
    }

    private static String asJsonString(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultActions performRequest(String login, String password) throws Exception {
        LoginRequest request = LoginRequest.builder().login(login).password(password).build();

        return mockMvc.perform(post(ENDPOINT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)));
    }
}