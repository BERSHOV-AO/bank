package ru.kata.bank.service;

import ru.kata.bank.model.entity.Client;

import java.util.UUID;

public interface ClientService {
    Client loadUserByLoginRequest(String login, String password);

    Client getUserById(UUID id);
}