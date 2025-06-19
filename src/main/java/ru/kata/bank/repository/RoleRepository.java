package ru.kata.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.bank.model.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
