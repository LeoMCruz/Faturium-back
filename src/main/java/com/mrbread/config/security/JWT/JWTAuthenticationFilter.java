package com.mrbread.config.security.JWT;

import com.mrbread.domain.model.Organizacao;
import com.mrbread.domain.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mrbread.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${mrbread.auth.password}")
    private String tokenPassword;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.info("[{}] '{}' from '{}'", request.getMethod(),
                request.getRequestURI(), request.getRemoteAddr());
        var token = validarToken(request);
        if (token.isPresent()) {
            var authentication = createToken(token.get());
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private Optional<DecodedJWT> validarToken(HttpServletRequest request) {
        var token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith(BEARER_PREFIX))
            return Optional.empty();
        var decodedToken = JWT.require(Algorithm.HMAC256(tokenPassword)).build()
                .verify(token.replace(BEARER_PREFIX, ""));
        return Optional.ofNullable(decodedToken);
    }

    private UsernamePasswordAuthenticationToken createToken(DecodedJWT value) {
        User user = userRepository.findByLogin(value.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        if(!user.isEnabled()){
            throw new DisabledException("Usuário está inativo");
        }
        List<SimpleGrantedAuthority> authorities = value.getClaim("roles").asList(String.class)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(user,null, authorities);
    }
}
