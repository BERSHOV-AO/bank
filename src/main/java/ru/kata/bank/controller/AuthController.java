package ru.kata.bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.bank.model.dto.JwtResponse;
import ru.kata.bank.model.dto.LoginRequest;
import ru.kata.bank.service.AuthBusinessService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthBusinessService authBusinessService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest authRequest) {
        String userId = authBusinessService.getUserIdByLogin(authRequest);
        log.debug("User \"{}\" is trying to log in", userId);
        JwtResponse response = authBusinessService.login(authRequest.login(), authRequest.password());
        log.debug("User \"{}\" successfully logged in", userId);
        return ResponseEntity.ok(response);
    }
}
