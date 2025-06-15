package ru.kata.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.bank.model.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
