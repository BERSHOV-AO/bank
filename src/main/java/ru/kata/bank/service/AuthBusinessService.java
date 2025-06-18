package ru.kata.bank.service;

import ru.kata.bank.model.dto.auth.JwtResponse;
import ru.kata.bank.model.dto.auth.LoginRequest;

public interface AuthBusinessService {
    JwtResponse login(String login, String password);

    String getUserIdByLogin(LoginRequest loginRequest);
}