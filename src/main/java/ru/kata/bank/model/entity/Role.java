package ru.kata.bank.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 * роль
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role implements GrantedAuthority {

    /**
     * id генерируется тут
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * имя роли
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * пользователи владеющие этой ролью
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    private Set<MisUser> misUsers;

    @Override
    public String getAuthority() {
        return name;
    }
}