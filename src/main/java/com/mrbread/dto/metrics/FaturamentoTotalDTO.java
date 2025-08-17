package com.mrbread.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaturamentoTotalDTO {
    private BigDecimal valorTotal;
    private String periodoReferencia;
    private BigDecimal ticketMedio;
}
