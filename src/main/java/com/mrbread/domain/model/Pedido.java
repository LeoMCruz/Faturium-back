package com.mrbread.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pedido implements PertenceOrganizacao{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, updatable = false)
    private Long idPedido;
    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ItemPedido> itens = new ArrayList<>();
    @Column
    private BigDecimal precoTotal;
    @ManyToOne
    @JoinColumn(name = "organizacao_id", referencedColumnName = "idOrg")
    private Organizacao organizacao;
    @ManyToOne
    @JoinColumn(name = "usuario_criacao", referencedColumnName = "login")
    private User user;
    @ManyToOne
    @JoinColumn(name= "cliente_id", referencedColumnName = "id")
    private Cliente cliente;
    @Column
    private String nomeFantasiaCliente;
    @Column
    private Status status;
    @Column
    private LocalDateTime dataCriacao;
    @Column
    private LocalDateTime dataAlteracao;
    @Column
    private String obs;
}
