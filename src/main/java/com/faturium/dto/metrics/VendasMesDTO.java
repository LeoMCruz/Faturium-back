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
public class VendasMesDTO {
    private BigDecimal valor;
    private Long quantidadePedidos;
    private BigDecimal percentualVariacao;
}
