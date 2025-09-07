package com.faturium.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaisVendidoDTO {
    private String nome;
    private String tipo;
    private Long quantidadeVendida;
    private BigDecimal valorTotal;
}
