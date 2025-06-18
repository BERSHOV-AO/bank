package ru.kata.bank.model.dto.auth;

import lombok.Builder;

@Builder
public record JwtResponse(String accessToken, String refreshToken) {
}
