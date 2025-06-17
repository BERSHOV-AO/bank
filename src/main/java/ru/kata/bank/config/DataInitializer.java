package ru.kata.bank.config;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.bank.model.entity.Client;
import ru.kata.bank.model.entity.Role;
import ru.kata.bank.model.enums.RoleNames;
import ru.kata.bank.repository.ClientRepository;
import ru.kata.bank.repository.RoleRepository;

import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", value = "init-fake-data", havingValue = "true")
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    private int number = 0;
    private Map<RoleNames, Role> rolesMap = new HashMap<>();


    private Map<RoleNames, Role> createRoles() {
        Map<RoleNames, Role> map = new EnumMap<>(RoleNames.class);
        for (RoleNames name : RoleNames.values()) {
            map.put(name, roleRepository.save(
                    Role.builder()
                            .name(name.name())
                            .build()));
        }
        return map;
    }

    private int getNumber() {
        return ++number;
    }

    private String generatePhone(long id) {
        StringBuilder builder = new StringBuilder();
        String code = "7909";
        builder.append(code);
        String number = String.valueOf(id);
        int zerosCount = 11 - (code.length() + number.length());
        for (int i = 0; i < zerosCount; i++) {
            builder.append("0");
        }
        return builder.append(number).toString();
    }

    private String generatePass(long id) {
        return passwordEncoder.encode(String.format("password-%s", id));
    }

    private Client createMisUser(String id, Set<Role> roles) {
        int number = getNumber();

        return clientRepository.save(
                Client.builder()
                        .id(UUID.fromString(id))
                        .login(generatePhone(number))
                        .isEnabled(true)
                        .password(generatePass(number))
                        .roles(roles)
                        .build()
        );
    }

    private Set<Role> getRole(Set<RoleNames> names) {
        return names.stream()
                .map(name -> rolesMap.get(name))
                .collect(Collectors.toSet());
    }

    @PostConstruct
    public void addFakeData() {
        rolesMap = createRoles();

        Client client1 = createMisUser("ac9360fd-75ba-46c1-81dd-b9f54962aca5", getRole(Set.of(RoleNames.CLIENT)));
    }
}
