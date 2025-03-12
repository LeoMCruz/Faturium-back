package com.mrbread.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Organizacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idOrg;
    @Column
    private String nomeOrganizacao;
    @Column (unique = true)
    private String cnpj;
    @Column
    private Status status;
    @OneToMany(mappedBy = "organizacao")
    @ToString.Exclude
    private Set<User> usuarios = new HashSet<>();
    @OneToMany(mappedBy = "organizacao")
    @ToString.Exclude
    private Set<Produto> produto = new HashSet<>();
    @Column
    private LocalDateTime dataCriacao;
    @Column
    private LocalDateTime dataAlteracao;
}
