package com.mrbread.dto;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoDTO {
    private Long id;
    private String nomeServico;
    private String descricao;
    private BigDecimal precoBase;
}
