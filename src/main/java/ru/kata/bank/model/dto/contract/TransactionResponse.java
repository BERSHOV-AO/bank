package ru.kata.bank.model.dto.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TransactionResponse {
    private String status;
}