package ru.kata.bank.service;

import ru.kata.bank.model.entity.MisUser;

import java.util.UUID;

public interface MisUserService {
    MisUser loadUserByLoginRequest(String login, String password);
    MisUser getUserById(UUID id);
}
