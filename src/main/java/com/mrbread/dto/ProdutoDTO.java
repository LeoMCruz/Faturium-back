package com.mrbread.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDTO {
    private Long id;
    private String nomeProduto;
    private String descricao;
    private BigDecimal precoBase;
    private UUID organizacaoId;
}
