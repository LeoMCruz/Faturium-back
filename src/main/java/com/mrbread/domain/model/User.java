package com.mrbread.domain.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails, PertenceOrganizacao {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    @Column
    private String senha;
    @Column(unique = true)
    private String login;
    @Column
    private String nome;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizacao_id", referencedColumnName = "idOrg")
    private Organizacao organizacao;
    @Column
    @Enumerated
    private PerfilAcesso perfilAcesso;
    @Column
    private String googleId;
    @Column
    private Boolean profileComplete;
    @Column
    private AuthProvider authProvider;
    @Column
    @Enumerated
    private Status status;
    @Column
    private LocalDateTime dataCriacao;
    @Column
    private LocalDateTime dataAlteracao;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        if(this.perfilAcesso == PerfilAcesso.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
//                new SimpleGrantedAuthority("ROLE_MANAGER"), new SimpleGrantedAuthority("ROLE_DEFAULT"));
//        else if (this.perfilAcesso == PerfilAcesso.MANAGER)  return List.of(new SimpleGrantedAuthority("ROLE_MANAGER"),
//                new SimpleGrantedAuthority("ROLE_DEFAULT"));
//        else return List.of(new SimpleGrantedAuthority("ROLE_DEFAULT"));
        return this.perfilAcesso.getRoles().stream().map(
                SimpleGrantedAuthority::new
        ).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == Status.ATIVO;
    }
}
