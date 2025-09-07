package com.faturium.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", referencedColumnName = "id")
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", referencedColumnName = "id")
    private Servico servico;

    @Column(nullable = false)
    private BigDecimal quantidade;

    @Column(nullable = false)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private BigDecimal precoTotal;

    @Column
    private Status status;
    @Column
    private String nome;
    @Column
    private String descricao;
    @Column
    private String tipo;
}