package ru.kata.bank.controller.outer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kata.bank.model.dto.JwtResponse;
import ru.kata.bank.model.dto.LoginRequest;
import ru.kata.bank.service.AuthBusinessService;

@Slf4j
@RestController
@PreAuthorize("hasAnyAuthority('CLIENT')")
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test")
    public String login() {
        return "DONE";
    }
}
