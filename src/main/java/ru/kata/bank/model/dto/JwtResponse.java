package ru.kata.bank.model.dto;

import lombok.Builder;

@Builder
public record JwtResponse(String accessToken, String refreshToken) {
}
