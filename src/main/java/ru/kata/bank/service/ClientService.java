package ru.kata.bank.service;

import ru.kata.bank.model.entity.Client;

public interface ClientService {
    Client loadUserByLoginRequest(String login, String password);
}