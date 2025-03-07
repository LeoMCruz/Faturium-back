package com.mrbread.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.User;
import com.mrbread.dto.AuthenticationRequest;
import com.mrbread.dto.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    @Value("${mrbread.auth.password}")
    private String tokenPassword;
    @Value("${mrbread.auth.expirationTime}")
    private Long tokenExpire;

    @Transactional
    public AuthenticationResponse generateToken(AuthenticationRequest oauthRequest){
        try {
            var authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(oauthRequest.getUsername(),
                            oauthRequest.getPassword()));

            var user = (User) authentication.getPrincipal();

            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            var token = JWT.create()
                    .withClaim("id", user.getId().toString())
                    .withSubject(oauthRequest.getUsername())
                    .withIssuedAt(new Date())
                    .withClaim("roles", roles)
                    .withAudience(user.getOrganizacao().getIdOrg().toString())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + tokenExpire * 1000))
                    .sign(Algorithm.HMAC256(tokenPassword));
            return AuthenticationResponse.builder().accessToken(token).build();

        }catch (BadCredentialsException e) {
            throw new AppException("Login ou Senha Inválidos","Login ou Senha Inválidos", HttpStatus.UNAUTHORIZED);
        } catch (DisabledException e) {
            throw new AppException("Usuário Inativo","Usuário Inativo", HttpStatus.FORBIDDEN);
        }
    }
}
