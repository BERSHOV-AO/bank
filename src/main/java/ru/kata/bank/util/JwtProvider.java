package ru.kata.bank.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.kata.bank.model.dto.auth.JwtAuthentication;
import ru.kata.bank.model.dto.auth.JwtAuthenticationDto;
import ru.kata.bank.model.entity.Client;
import ru.kata.bank.model.entity.Role;
import ru.kata.bank.model.enums.RoleNames;
import ru.kata.bank.model.exception.UnauthorizedException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final long jwtAccessExpiration;
    private final ObjectMapper objectMapper;

    public JwtProvider(@Value("${jwt.access.secret}") String jwtAccessSecret,
                       @Value("${jwt.access.expiration}") long jwtAccessExpiration,
                       ObjectMapper objectMapper) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtAccessExpiration = jwtAccessExpiration;
        this.objectMapper = objectMapper;
    }

    public String generateAccessToken(Client user) {
        return Jwts.builder()
                .signWith(jwtAccessSecret)
                .setPayload(createPayload(user, jwtAccessExpiration))
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtAccessSecret)
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (Exception e) {
            log.error("invalid access token", e);
        }
        return false;
    }

    private String createPayload(Client user, long expiration) {
        try {
            return objectMapper.writeValueAsString(
                    JwtAuthenticationDto.builder()
                            .user(user.getId())
                            .claims(getRolesNames(user))
                            .createDate(getCreateDate())
                            .expirationDate(getExpirationDate(expiration))
                            .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getRolesNames(Client user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .toList();
    }

    private Date getExpirationDate(long seconds) {
        return Date.from(LocalDateTime.now().plusSeconds(seconds).atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date getCreateDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    private boolean validateRequest(JwtAuthenticationDto requestDto) {
        if (requestDto == null) {
            throw new UnauthorizedException("Request data is empty");
        }
        if (requestDto.user() == null) {
            throw new UnauthorizedException("User is empty");
        }
        if (new Date().after(requestDto.expirationDate())) {
            throw new UnauthorizedException("Token is expired");
        }
        return true;
    }

    public JwtAuthentication getAuthentication(String accessToken) throws JsonProcessingException {
        String[] chunks = accessToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]), StandardCharsets.UTF_8);
        JwtAuthenticationDto requestDto = objectMapper.readValue(payload, JwtAuthenticationDto.class);
        JwtAuthentication jwtInfoToken = JwtAuthentication.builder()
                .authenticated(validateRequest(requestDto))
                .build();
        return generate(jwtInfoToken, requestDto);
    }

    private JwtAuthentication generate(JwtAuthentication jwtInfoToken, JwtAuthenticationDto requestDto) {
        if (jwtInfoToken.isAuthenticated()) {
            jwtInfoToken.setUserId(requestDto.user());
            jwtInfoToken.setRoles(getRoles(requestDto.claims()));
        }
        return jwtInfoToken;
    }

    private Set<Role> getRoles(List<String> claims) {
        return Arrays.stream(RoleNames.values())
                .map(Enum::name)
                .toList()
                .stream()
                .filter(claims::contains)
                .map(name -> {
                    Role role = new Role();
                    role.setName(name);
                    return role;
                })
                .collect(Collectors.toSet());
    }

    public String getTokenFromRequest(String bearer) {
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
