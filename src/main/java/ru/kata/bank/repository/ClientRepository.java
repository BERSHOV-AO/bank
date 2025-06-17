package ru.kata.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kata.bank.model.entity.Client;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    @Query("""
            select c
            from Client c
                join fetch c.roles
            where c.login = :login
                and c.isEnabled = true
            """)
    Client findByLoginWithRoles(String login);
}