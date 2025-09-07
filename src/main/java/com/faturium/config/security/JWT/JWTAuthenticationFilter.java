package com.faturium.config.security.JWT;

import com.faturium.config.exception.AppException;
import com.faturium.domain.model.Organizacao;
import com.faturium.domain.model.PerfilAcesso;
import com.faturium.domain.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.faturium.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${faturium.auth.password}")
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
//        User user = userRepository.findByLogin(value.getSubject())
//                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        User user = new User();
        user.setId(UUID.fromString(value.getClaim("id").asString()));
        if (value.getAudience() != null && !value.getAudience().isEmpty()) {
            user.setOrganizacao(Organizacao.builder()
                    .idOrg(UUID.fromString(value.getAudience().get(0)))
                    .build());
        } else {
            user.setOrganizacao(null);
        }
        user.setLogin(value.getSubject());
//        if(!user.isEnabled()){
//            throw new DisabledException("Usuário está inativo");
//        }
        var roleList = value.getClaim("roles").asList(String.class);

        List<SimpleGrantedAuthority> authorities = roleList
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        var perfilAcesso = Arrays.stream(PerfilAcesso.values())
                .filter(x -> x.getRoles().equals(roleList))
                .findFirst().orElseThrow(() -> new AppException("Invalid Access",
                        "Roles dont match",
                        HttpStatus.FORBIDDEN));

        user.setPerfilAcesso(perfilAcesso);



        return new UsernamePasswordAuthenticationToken(user,null, authorities);
    }
}
