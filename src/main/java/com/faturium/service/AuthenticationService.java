package com.faturium.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.faturium.config.exception.AppException;
import com.faturium.domain.model.AuthProvider;
import com.faturium.domain.model.User;
import com.faturium.domain.repository.UserRepository;
import com.faturium.dto.AuthenticationRequest;
import com.faturium.dto.AuthenticationResponse;
import com.faturium.dto.GoogleAuthRequest;
import com.faturium.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.auth.oauth2.TokenVerifier;
import com.google.auth.oauth2.TokenVerifier.VerificationException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    @Value("${faturium.auth.password}")
    private String tokenPassword;
    @Value("${faturium.auth.expirationTime}")
    private Long tokenExpire;
    @Value("${google.client-id}")
    private String clientId;

    @Transactional
    public AuthenticationResponse generateToken(AuthenticationRequest oauthRequest){
        try {
            var authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(oauthRequest.getUsername(),
                            oauthRequest.getPassword()));

            var user = (User) authentication.getPrincipal();

            return generateJWT(user);

        }catch (BadCredentialsException e) {
            throw new AppException("Login ou Senha Inválidos","Login ou Senha Inválidos", HttpStatus.UNAUTHORIZED);
        } catch (DisabledException e) {
            throw new AppException("Usuário Inativo","Usuário Inativo", HttpStatus.FORBIDDEN);
        }
    }

    public AuthenticationResponse verifyGoogleAccount(GoogleAuthRequest googleAuthRequest){
        try {
            var token = verifyToken(googleAuthRequest.getIdToken());
            var payload = token.getPayload();
            var email = (String) payload.get("email");
            var name = (String) payload.get("name");
            var sub = (String) payload.get("sub");

            Optional<User> existingUser = userRepository.findByLogin(email);

            if (existingUser.isPresent()) {
                User user = existingUser.get();

                if (user.getGoogleId().equals(sub) ) {
                    return generateJWT(user);
                } else {
                    throw new AppException(
                            "Conta existente",
                            "Este email já possui uma conta. Faça login com sua senha para vincular ao Google.",
                            HttpStatus.CONFLICT
                    );
                }

            } else {
                var user = userService.createGoogleUser(email, sub, name);
                return generateJWT(user);
            }

        } catch (VerificationException e) {
            throw new AppException("Token Google inválido", e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (AppException e) {
            throw e; // Re-throw AppException para manter o status específico
        } catch (Exception e) {
            throw new AppException("Falha na autenticação Google", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AuthenticationResponse linkGoogleId(GoogleAuthRequest googleAuthRequest){
        try{
            var token = verifyToken(googleAuthRequest.getIdToken());
            var payload = token.getPayload();
            var email = (String) payload.get("email");
            var sub = (String) payload.get("sub");
            if (!email.equals(googleAuthRequest.getUsername()))
                throw new AppException("Emails nao coincidem", "Os emails são diferentes", HttpStatus.BAD_REQUEST);

            var authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(googleAuthRequest.getUsername(),
                            googleAuthRequest.getPassword()));

            var user = (User) authentication.getPrincipal();

            user.setGoogleId(sub);
            user.setAuthProvider(AuthProvider.BOTH);
            userRepository.save(user);
            return generateJWT(user);


        }catch (BadCredentialsException e) {
            throw new AppException("Login ou Senha Inválidos","Login ou Senha Inválidos", HttpStatus.UNAUTHORIZED);
        } catch (DisabledException e) {
            throw new AppException("Usuário Inativo","Usuário Inativo", HttpStatus.FORBIDDEN);
        } catch (VerificationException e) {
            throw new AppException("Token Google inválido", e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (AppException e) {
            throw e; // Re-throw AppException para manter o status específico
        } catch (Exception e) {
            throw new AppException("Falha na autenticação Google", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private AuthenticationResponse generateJWT(User user) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        var token = JWT.create()
                .withClaim("id", user.getId().toString())
                .withSubject(user.getLogin())
                .withIssuedAt(new Date())
                .withClaim("roles", roles)
                .withAudience(user.getOrganizacao() != null ?
                        user.getOrganizacao().getIdOrg().toString() : null)
                .sign(Algorithm.HMAC256(tokenPassword));

        return AuthenticationResponse.builder()
                .accessToken(token)
                .profileComplete(user.getProfileComplete())
                .build();
    }

    private JsonWebSignature verifyToken(String idToken) throws Exception {
        String cleanToken = idToken.replaceAll("\\s+", "");

        var verifier = TokenVerifier.newBuilder()
                .setAudience(clientId)
                .setIssuer("https://accounts.google.com")
                .build();

        var token = verifier.verify(cleanToken);
        var payload = token.getPayload();

        var emailVerified = Boolean.TRUE.equals(payload.get("email_verified"));
        if (!emailVerified) {
            throw new IllegalArgumentException("Email não verificado");
        }

        return token;
    }

    public AuthenticationResponse completeRegister(UserDTO userDTO){
        var user = userService.completeProfile(userDTO);
        return generateJWT(user);
    }
}
