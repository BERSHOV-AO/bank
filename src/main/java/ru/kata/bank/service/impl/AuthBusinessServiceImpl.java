package ru.kata.bank.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kata.bank.model.dto.JwtResponse;
import ru.kata.bank.model.dto.LoginRequest;
import ru.kata.bank.model.entity.MisUser;
import ru.kata.bank.service.AuthBusinessService;
import ru.kata.bank.service.MisUserService;
import ru.kata.bank.util.JwtProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthBusinessServiceImpl implements AuthBusinessService {
    private final MisUserService misUserService;
    private final JwtProvider jwtProvider;

    @Override
    public JwtResponse login(String login, String password) {
        MisUser user = misUserService.loadUserByLoginRequest(login, password);

        return JwtResponse.builder()
                .accessToken(jwtProvider.generateAccessToken(user))
                .refreshToken(jwtProvider.generateRefreshToken(user))
                .build();
    }

    @Override
    public String getUserIdByLogin(LoginRequest loginRequest) {
        return misUserService.loadUserByLoginRequest(loginRequest.login(), loginRequest.password()).getId().toString();
    }
}
