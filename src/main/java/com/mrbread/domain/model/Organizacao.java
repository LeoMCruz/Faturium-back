package com.mrbread.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
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
}
