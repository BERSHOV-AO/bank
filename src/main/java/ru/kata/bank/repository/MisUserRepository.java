package ru.kata.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.bank.model.entity.MisUser;

import java.util.UUID;

public interface MisUserRepository extends JpaRepository<MisUser, UUID> {
}
