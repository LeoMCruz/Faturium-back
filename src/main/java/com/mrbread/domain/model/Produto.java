package com.mrbread.domain.model;

import jakarta.persistence.*;
import jdk.jfr.Registered;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nomeProduto;
    @Column
    private String descricao;
    @Column
    private BigDecimal precoBase;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizacao_id", referencedColumnName = "idOrg")
    private Organizacao organizacao;
}
