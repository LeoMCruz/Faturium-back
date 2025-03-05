package com.mrbread.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.BiFunction;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    private UUID idPedido;
    @ManyToOne
    @JoinColumn(name = "produto_id",referencedColumnName = "id")
    private Produto produto;
    @ManyToOne
    @JoinColumn(name = "servico_id", referencedColumnName = "id")
    private Servico servico;
    @Column
    private BigDecimal quantidade;
    @Column
    private BigDecimal precoUnitario;
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
    private Status status;
    @Column
    private LocalDateTime dataCriacao;
    @Column
    private LocalDateTime dataAlteracao;

}
