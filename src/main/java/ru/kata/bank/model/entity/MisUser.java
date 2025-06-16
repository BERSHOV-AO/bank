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
@Table(name = "mis_users")
public class MisUser {

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
     * роли пользователя MisUser
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mis_users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
