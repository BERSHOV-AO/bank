package ru.kata.bank.model.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record TokenDateInfo(Date createToken, long expirationToken) {
}
