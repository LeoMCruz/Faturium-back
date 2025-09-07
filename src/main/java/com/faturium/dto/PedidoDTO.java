package com.faturium.dto;

import com.faturium.domain.model.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {
    private UUID id;
    private Long idPedido;
    private List<ItemPedidoDTO> itens;
    private BigDecimal precoTotal;
    private UUID organizacao;
    private String user;
    private UUID cliente;
    private String nomeFantasiaCliente;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
    private String obs;
}
