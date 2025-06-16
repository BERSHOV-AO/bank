package ru.kata.bank.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.bank.model.entity.MisUser;
import ru.kata.bank.model.exception.UnauthorizedException;
import ru.kata.bank.repository.MisUserRepository;
import ru.kata.bank.service.MisUserService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MisUserServiceImpl implements MisUserService {

    private final MisUserRepository misUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MisUser loadUserByLoginRequest(String login, String password) {
        MisUser misUser = misUserRepository.findByLoginWithRoles(login);
        validate(login, password, misUser);
        return misUser;
    }

    @Override
    public MisUser getUserById(UUID id) {
        return misUserRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found by id: " + id));
    }

    private void validate(String login, String password, MisUser misUser) {
        if (misUser == null) {
            log.info("Authenticate account with login {} failed. Reason -> invalid login", login);
            throw new UnauthorizedException(String.format("Активный пользователь MIS с логином %s не найден", login));
        }

        if (!passwordEncoder.matches(password, misUser.getPassword())) {
            log.info("Authenticate account with login {} failed. Reason -> password incorrect", login);
            throw new UnauthorizedException("Пароль не совпадает");
        }
    }
}