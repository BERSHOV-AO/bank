package ru.kata.bank.controller_outer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import ru.kata.bank.ContextIT;
import ru.kata.bank.model.dto.auth.JwtAuthentication;
import ru.kata.bank.model.entity.Role;
import ru.kata.bank.model.enums.TransactionStatus;
import ru.kata.bank.util.JwtProvider;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GetTransactionIT extends ContextIT {

    @MockBean
    private JwtProvider jwtProvider;

    private static final String ROLE = "CLIENT";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ENDPOINT_URL = "/api/bank/transaction/status";
    private static final String ACCESS_TOKEN = "Bearer test-token";
    private static final String MOCK_JWT_TOKEN = "test-token";
    private static final String USER_ID = "ac9360fd-75ba-46c1-81dd-b9f54962aca5";
    private static final Long NUMBER_TRANSACTION = 7777777L;


    public void settingJwtMocks(UUID userId, Role role, boolean isTokenValid) throws JsonProcessingException {
        JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setUserId(userId);
        jwtInfoToken.setRoles(Set.of(role));
        jwtInfoToken.setAuthenticated(isTokenValid);

        when(jwtProvider.getTokenFromRequest(ACCESS_TOKEN)).thenReturn(MOCK_JWT_TOKEN);
        when(jwtProvider.validateAccessToken(MOCK_JWT_TOKEN)).thenReturn(isTokenValid);
        when(jwtProvider.getAuthentication(MOCK_JWT_TOKEN)).thenReturn(isTokenValid ? jwtInfoToken : null);
    }

    private ResultActions performRequest(long numberTransaction) throws Exception {
        return mockMvc.perform(get(ENDPOINT_URL)
                .param("numberTransaction", String.valueOf(numberTransaction))
                .header(AUTHORIZATION_HEADER, ACCESS_TOKEN));
    }

    @Test
    @DisplayName("Получать статус, успешный")
    public void getTransaction_Success() throws Exception {
        settingJwtMocks(UUID.fromString(USER_ID), Role.builder().name(ROLE).build(), true);
        performRequest(NUMBER_TRANSACTION)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(TransactionStatus.DONE.toString()));
    }
}