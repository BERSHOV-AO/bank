package ru.kata.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kata.bank.model.entity.MisUser;

import java.util.UUID;

public interface MisUserRepository extends JpaRepository<MisUser, UUID> {
    @Query("""
            select u
            from MisUser u
                join fetch u.roles
            where u.login = :login
                and u.isEnabled = true
            """)
    MisUser findByLoginWithRoles(String login);
}
