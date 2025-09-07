package com.faturium.dto;

import com.faturium.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {
    private UUID id;
    private String nomeProduto;
    private String descricao;
    private BigDecimal precoBase;
    private UUID organizacaoId;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
}
