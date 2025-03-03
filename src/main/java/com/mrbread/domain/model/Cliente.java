package com.mrbread.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    private String nomeFantasia;
    @Column
    private String razaoSocial;
    @Column
    private String cnpj;
    @Column
    private String endereco;
    @Column
    private String cidade;
    @Column
    private String estado;
    @Column
    private String email;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizacao_id", referencedColumnName = "idOrg")
    private Organizacao organizacao;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_criacao", referencedColumnName = "login")
    private User usuarioCriacao;
    @Column
    private Status status;
}
