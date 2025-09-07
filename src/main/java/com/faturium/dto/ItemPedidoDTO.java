package com.faturium.dto;

import com.faturium.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedidoDTO {
    private UUID id;
    private Long pedido;
    private UUID produto;
    private UUID servico;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal precoTotal;
    private Status status;

    // Campos adicionados para uso em relatórios
    private String nome;
    private String descricao;
    private String tipo;
}
