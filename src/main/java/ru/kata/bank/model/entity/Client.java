package ru.kata.bank.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    /**
     * роли пользователя Client
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "client_roles",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}