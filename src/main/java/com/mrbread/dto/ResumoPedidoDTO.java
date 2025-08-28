package com.mrbread.dto;

import com.mrbread.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumoPedidoDTO {
    private UUID id;
    private Long idPedido;
    private BigDecimal precoTotal;
    private String cliente;
    private String razaoSocial;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
    private String usuarioCriacao;
}
