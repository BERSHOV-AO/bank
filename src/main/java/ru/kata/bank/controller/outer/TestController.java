package ru.kata.bank.controller.outer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
