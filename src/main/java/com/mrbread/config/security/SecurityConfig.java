package com.mrbread.config.security;

import com.mrbread.config.security.JWT.JWTAuthenticationFilter;
import com.mrbread.config.security.JWT.JwtUserDetailsService;
import com.mrbread.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

    private final JwtUserDetailsService JWTUserDetailsService;
//    private final JWTAuthenticationFilter authenticationFilter;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configuração de CORS e CSRF
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)

                // Configuração das regras de autorização
                .authorizeHttpRequests(auth -> auth
                        // Permitir acesso público ao endpoint de login e criação de usuário
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        // permitir acesso ao h2
//                        .requestMatchers("/h2-console/**").permitAll()

                        // Qualquer outra requisição precisa de autenticação
                        .anyRequest().authenticated()
                )

                // Configuração do gerenciamento de sessão
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Adiciona o filtro JWT antes do filtro padrão de autenticação
                .addFilterBefore(authenticationFilter(userRepository), UsernamePasswordAuthenticationFilter.class);

        // Configuração h2
//        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    public JWTAuthenticationFilter authenticationFilter(UserRepository userRepository) {
        return new JWTAuthenticationFilter(userRepository);
    }

    @Bean
    @SneakyThrows
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Configura o serviço de usuários e o encoder de senha
        authProvider.setUserDetailsService(JWTUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
