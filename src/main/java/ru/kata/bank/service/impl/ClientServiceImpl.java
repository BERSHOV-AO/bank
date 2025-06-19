package ru.kata.bank.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.bank.model.entity.Client;
import ru.kata.bank.model.exception.UnauthorizedException;
import ru.kata.bank.repository.ClientRepository;
import ru.kata.bank.service.ClientService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Client loadUserByLoginRequest(String login, String password) {
        Client client = clientRepository.findByLoginWithRoles(login);
        validate(login, password, client);
        return client;
    }

    private void validate(String login, String password, Client client) {
        if (client == null) {
            log.error("Authenticate account with login {} failed. Reason -> invalid login", login);
            throw new UnauthorizedException(String.format("Активный пользователь MIS с логином %s не найден", login));
        }

        if (!passwordEncoder.matches(password, client.getPassword())) {
            log.error("Authenticate account with login {} failed. Reason -> password incorrect", login);
            throw new UnauthorizedException("Пароль не совпадает");
        }
    }
}