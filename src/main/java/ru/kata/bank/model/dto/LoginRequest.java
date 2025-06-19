package ru.kata.bank.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(@NotBlank String login, @NotBlank String password) {
}
