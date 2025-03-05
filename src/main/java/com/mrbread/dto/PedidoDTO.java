package com.mrbread.dto;

import com.mrbread.domain.model.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {
    private UUID id;
    private UUID idPedido;
    private UUID produto;
    private UUID servico;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal precoTotal;
    private UUID organizacao;
    private String user;
    private UUID cliente;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
}
