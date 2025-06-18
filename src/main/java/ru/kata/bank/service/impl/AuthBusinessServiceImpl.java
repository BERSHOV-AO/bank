package ru.kata.bank.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kata.bank.model.dto.auth.JwtResponse;
import ru.kata.bank.model.dto.auth.LoginRequest;
import ru.kata.bank.model.entity.Client;
import ru.kata.bank.service.AuthBusinessService;
import ru.kata.bank.service.ClientService;
import ru.kata.bank.util.JwtProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthBusinessServiceImpl implements AuthBusinessService {
    private final ClientService clientService;
    private final JwtProvider jwtProvider;

    @Override
    public JwtResponse login(String login, String password) {
        Client client = clientService.loadUserByLoginRequest(login, password);

        return JwtResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(client))
                .refreshToken(jwtProvider.generateRefreshToken(client))
                .build();
    }

    @Override
    public String getUserIdByLogin(LoginRequest loginRequest) {
        return clientService.loadUserByLoginRequest(loginRequest.login(), loginRequest.password()).getId().toString();
    }
}