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
    @Pattern(regexp = "^(\\d{2})\\.(\\d{3})\\.(\\d{3})\\/\\d{4}\\-\\d{2}$", message = "CNPJ inválido. O formato deve ser XX.XXX.XXX/XXXX-XX")
    private String cnpj;
    private Status status;
    @OneToMany(mappedBy = "organizacao")
    @ToString.Exclude
    private Set<User> usuarios = new HashSet<>();
    @OneToMany(mappedBy = "organizacao")
    @ToString.Exclude
    private Set<Produto> produto = new HashSet<>();
}
