package ru.kata.bank.service;

import ru.kata.bank.model.entity.User;

import java.util.UUID;

public interface ClientService {
    User loadUserByLoginRequest(String login, String password);

    User getUserById(UUID id);
}