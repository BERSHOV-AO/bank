package ru.kata.bank.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.kata.bank.model.dto.auth.JwtAuthentication;
import ru.kata.bank.model.dto.auth.JwtAuthenticationDto;
import ru.kata.bank.model.dto.TokenDateInfo;
import ru.kata.bank.model.entity.Role;
import ru.kata.bank.model.entity.User;
import ru.kata.bank.model.enums.RoleNames;
import ru.kata.bank.model.exception.UnauthorizedException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    private final long jwtAccessExpiration;
    private final long jwtRefreshExpiration;
    private final ObjectMapper objectMapper;

    public JwtProvider(@Value("${jwt.access.secret}") String jwtAccessSecret,
                       @Value("${jwt.refresh.secret}") String jwtRefreshSecret,
                       @Value("${jwt.access.expiration}") long jwtAccessExpiration,
                       @Value("${jwt.refresh.expiration}") long jwtRefreshExpiration,
                       ObjectMapper objectMapper) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.jwtAccessExpiration = jwtAccessExpiration;
        this.jwtRefreshExpiration = jwtRefreshExpiration;
        this.objectMapper = objectMapper;
    }

    public UUID getUserIdFromRefreshToken(String refreshToken) throws JsonProcessingException {
        String[] chunks = refreshToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]), StandardCharsets.UTF_8);
        JwtAuthenticationDto requestDto = objectMapper.readValue(payload, JwtAuthenticationDto.class);
        validateRequest(requestDto);
        return requestDto.user();
    }

    public TokenDateInfo getDateToken(String refreshToken) throws JsonProcessingException {
        String[] chunks = refreshToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]), StandardCharsets.UTF_8);
        JwtAuthenticationDto requestDto = objectMapper.readValue(payload, JwtAuthenticationDto.class);
        validateRequest(requestDto);
        return TokenDateInfo.builder().createToken(requestDto.createDate())
                .expirationToken(requestDto.expirationDate().getTime() - getCreateDate().getTime())
                .build();
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .signWith(jwtAccessSecret)
                .setPayload(createPayload(user, jwtAccessExpiration))
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .signWith(jwtRefreshSecret)
                .setPayload(createPayload(user, jwtRefreshExpiration))
                .compact();
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtRefreshSecret)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (Exception e) {
            log.error("invalid refresh token", e);
        }
        return false;
    }

    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtAccessSecret)
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private String createPayload(User user, long expiration) {
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

    private List<String> getRolesNames(User user) {
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
            throw new BadCredentialsException("Request data is empty");
        }
        if (requestDto.user() == null) {
            throw new BadCredentialsException("User is empty");
        }
        if (new Date().after(requestDto.expirationDate())) {
            throw new BadCredentialsException("Token is expired");
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
                .map(role -> new Role())
                .collect(Collectors.toSet());
    }
//private Set<Role> getRoles(List<String> claims) {
//    return Arrays.stream(RoleNames.values())
//            .map(Enum::name)
//            .toList()
//            .stream()
//            .filter(claims::contains)
//            .map(Role::new)
//            .collect(Collectors.toSet());
//}

    public String getTokenFromRequest(String bearer) {
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
