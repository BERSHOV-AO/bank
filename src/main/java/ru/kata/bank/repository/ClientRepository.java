package ru.kata.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kata.bank.model.entity.User;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<User, UUID> {
    @Query("""
            select c
            from User c
                join fetch c.roles
            where c.login = :login
                and c.isEnabled = true
            """)
    User findByLoginWithRoles(String login);
}