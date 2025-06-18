package ru.kata.bank.controller.outer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.bank.model.dto.contract.TransactionResponse;

@Slf4j
@RestController
//@PreAuthorize("hasAnyAuthority('CLIENT')")
@PreAuthorize("hasAnyRole('CLIENT')")
@RequestMapping("/api/bank/transaction")
@RequiredArgsConstructor
public class TransactionController {

    @GetMapping("/status")
    public ResponseEntity<TransactionResponse> getTransactionStatus(@RequestParam long numberTransaction) {
        return ResponseEntity.ok(new TransactionResponse("DONE: " + numberTransaction));
    }
}
