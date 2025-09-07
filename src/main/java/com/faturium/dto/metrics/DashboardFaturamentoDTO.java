package com.faturium.dto.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFaturamentoDTO {
    private VendasHojeDTO vendasHoje;
    private VendasMesDTO vendasMes;
    private ClientesAtivosDTO clientesAtivos;
    private ItensVendidosDTO itensVendidos;
    private List<MaisVendidoDTO> maisVendidos;
    private FaturamentoTotalDTO faturamentoTotal;
}
