package com.mrbread.dto;

import com.mrbread.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalhesPedidoDTO {
    private UUID id;
    private Long idPedido;
    private List<ItemPedidoDTO> itens;
    private BigDecimal precoTotal;
    private UUID organizacao;
    private String user;
    private UUID cliente;
    private String nomeFantasiaCliente;
    private String cnpj;
    private String cidade;
    private String estado;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
    private String usuarioCriacao;
    private String obs;
}
