package ru.kata.bank.model.dto.auth;

import lombok.Builder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
public record JwtAuthenticationDto(UUID user, List<String> claims, Date createDate, Date expirationDate) {
}