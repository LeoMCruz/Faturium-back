package com.mrbread.service;

import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.repository.PedidoRepository;
import com.mrbread.dto.metrics.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;

    @Transactional(readOnly = true)
    public DashboardFaturamentoDTO obterDashboard() {
        UUID orgId = SecurityUtils.obterOrganizacaoId();

        String username = SecurityUtils.isDefault() ? SecurityUtils.getEmail() : null;

        return DashboardFaturamentoDTO.builder()
                .vendasHoje(calcularVendasHoje(orgId, username))
                .vendasMes(calcularVendasMes(orgId, username))
                .clientesAtivos(clienteService.calcularClientesAtivos(orgId, username))
                .itensVendidos(calcularItensVendidos(orgId, username))
                .maisVendidos(calcularMaisVendidos(orgId, username))
                .faturamentoTotal(calcularFaturamentoTotal(orgId, username))
                .build();
    }

    private FaturamentoTotalDTO calcularFaturamentoTotal(UUID orgId, String username) {
        // alterar para pegar o ultimo ano de forma dinamica
        LocalDateTime dataInicio = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        Object[] result = pedidoRepository.findFaturamentoTotal(orgId, dataInicio, username);

        // Corrigir estrutura aninhada
        Object[] dados = (Object[]) result[0];
        
        long totalPedidos = ((Number) dados[0]).longValue();
        BigDecimal faturamentoTotal = (BigDecimal) dados[1];

        BigDecimal ticketMedio = BigDecimal.ZERO;
        if (totalPedidos > 0) {
            ticketMedio = faturamentoTotal.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP);
        }

        return FaturamentoTotalDTO.builder()
                .valorTotal(faturamentoTotal)
                .periodoReferencia("desde janeiro/2024")
                .ticketMedio(ticketMedio)
                .build();
    }

    private List<MaisVendidoDTO> calcularMaisVendidos(UUID orgId, String username) {
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);

        List<Object[]> results = pedidoRepository.findTop3MaisVendidos(orgId, inicioMesAtual, fimMesAtual, username);
        List<MaisVendidoDTO> maisVendidos = new ArrayList<>();

        for (Object[] result : results) {
            // Para List<Object[]>, os elementos já vêm corretos
            maisVendidos.add(MaisVendidoDTO.builder()
                    .nome((String) result[0])
                    .tipo((String) result[1])
                    .quantidadeVendida(((Number) result[2]).longValue())
                    .valorTotal((BigDecimal) result[3])
                    .build());
        }
        return maisVendidos;
    }

    private BigDecimal calcularVariacaoPercentual(BigDecimal valorAtual, BigDecimal valorAnterior) {
        if (valorAnterior == null || valorAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return valorAtual.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }

        if (valorAtual == null) {
            valorAtual = BigDecimal.ZERO;
        }

        return valorAtual.subtract(valorAnterior)
                .divide(valorAnterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private ItensVendidosDTO calcularItensVendidos(UUID orgId, String username) {
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);
        LocalDateTime inicioMesAnterior = inicioMesAtual.minusMonths(1);

        Long itensMesAtual = pedidoRepository.findItensVendidosMesAtual(orgId, inicioMesAtual, fimMesAtual, username);
        Long itensMesAnterior = pedidoRepository.findItensVendidosMesAnterior(orgId, inicioMesAnterior, inicioMesAtual, username);

        BigDecimal variacao = calcularVariacaoPercentual(
                BigDecimal.valueOf(itensMesAtual),
                BigDecimal.valueOf(itensMesAnterior)
        );
        return ItensVendidosDTO.builder()
                .quantidade(itensMesAtual)
                .percentualVariacao(variacao)
                .build();
    }

    public VendasMesDTO calcularVendasMes(UUID orgId, String username) {
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);
        LocalDateTime inicioMesAnterior = inicioMesAtual.minusMonths(1);

        Object[] mesAtual = pedidoRepository.findVendasMesAtual(orgId, inicioMesAtual, fimMesAtual, username);
        Object[] mesAnterior = pedidoRepository.findVendasMesAnterior(orgId, inicioMesAnterior, inicioMesAtual, username);

        // Corrigir estrutura aninhada
        Object[] dadosMesAtual = (Object[]) mesAtual[0];
        Object[] dadosMesAnterior = (Object[]) mesAnterior[0];

        Long quantidadeMesAtual = ((Number) dadosMesAtual[0]).longValue();
        BigDecimal valorMesAtual = (BigDecimal) dadosMesAtual[1];

        BigDecimal valorMesAnterior = (BigDecimal) dadosMesAnterior[1];

        BigDecimal variacao = calcularVariacaoPercentual(valorMesAtual, valorMesAnterior);

        return VendasMesDTO.builder()
                .valor(valorMesAtual)
                .quantidadePedidos(quantidadeMesAtual)
                .percentualVariacao(variacao)
                .build();
    }

    private VendasHojeDTO calcularVendasHoje(UUID orgId, String username) {
        LocalDateTime inicioHoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimHoje = inicioHoje.plusDays(1);
        LocalDateTime inicioOntem = inicioHoje.minusDays(1);

        Object[] hoje = pedidoRepository.findVendasHoje(orgId, inicioHoje, fimHoje, username);
        Object[] ontem = pedidoRepository.findVendasOntem(orgId, inicioOntem, inicioHoje, username);

        // Corrigir estrutura aninhada
        Object[] dadosHoje = (Object[]) hoje[0];  // Pegar o array interno
        Object[] dadosOntem = (Object[]) ontem[0]; // Pegar o array interno

        Long quantidadeHoje = ((Number) dadosHoje[0]).longValue();
        BigDecimal valorHoje = (BigDecimal) dadosHoje[1];

        BigDecimal valorOntem = (BigDecimal) dadosOntem[1];

        BigDecimal variacao = calcularVariacaoPercentual(valorHoje, valorOntem);

        return VendasHojeDTO.builder()
                .valor(valorHoje)
                .quantidadePedidos(quantidadeHoje)
                .percentualVariacao(variacao)
                .build();
    }
}
