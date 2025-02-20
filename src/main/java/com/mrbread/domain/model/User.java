package com.mrbread.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
