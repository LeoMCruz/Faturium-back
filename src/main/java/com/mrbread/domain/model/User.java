package com.mrbread.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
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
    @Enumerated
    private Status status;
    @Column
    private LocalDateTime dataCriacao;
    @Column
    private LocalDateTime dataAlteracao;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.perfilAcesso == PerfilAcesso.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), 
                new SimpleGrantedAuthority("ROLE_MANAGER"), new SimpleGrantedAuthority("ROLE_DEFAULT"));
        else if (this.perfilAcesso == PerfilAcesso.MANAGER)  return List.of(new SimpleGrantedAuthority("ROLE_MANAGER"),
                new SimpleGrantedAuthority("ROLE_DEFAULT"));
        else return List.of(new SimpleGrantedAuthority("ROLE_DEFAULT"));
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
