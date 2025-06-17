package ru.kata.bank.service;

import ru.kata.bank.model.dto.JwtResponse;
import ru.kata.bank.model.dto.LoginRequest;

public interface AuthBusinessService {
    JwtResponse login(String login, String password);

    String getUserIdByLogin(LoginRequest loginRequest);
}