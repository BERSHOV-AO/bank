package ru.kata.bank.util;

import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.kata.bank.model.dto.auth.JwtAuthentication;
import ru.kata.bank.model.exception.UnauthorizedException;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain) throws IOException, ServletException {
        String bearer = request.getHeader(AUTHORIZATION_HEADER);
        String token = jwtProvider.getTokenFromRequest(bearer);
        if (token != null) {
            if (jwtProvider.validateAccessToken(token)) {
                JwtAuthentication authentication = jwtProvider.getAuthentication(token);
                if (authentication.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    Objects.requireNonNull(filterChain).doFilter(request, response);
                } else {
                    throw new BadCredentialsException("Access token forbidden");
                }
            } else {
                throw new BadCredentialsException("Access token expired");
            }
        } else {
            Objects.requireNonNull(filterChain).doFilter(request, response);
        }
    }
}