# 📊 Dashboard de Faturamento - MrBread

## 🎯 Estrutura da Tela Analisada

Baseado na tela fornecida, identificamos as seguintes métricas:

### 📈 Cards Principais
1. **Vendas de Hoje** - R$ 150,00 (5 pedidos realizados) +12%
2. **Vendas do Mês** - R$ 3.500,00 (45 pedidos no período) +8%
3. **Clientes Ativos** - 12 (este mês) +3
4. **Itens Vendidos** - 101 (produtos e serviços) +15%

### 🏆 Mais Vendidos (Top 3)
1. **Pão Francês** - 45 unidades - R$ 22,50
2. **Bolo de Chocolate** - 8 unidades - R$ 120,00
3. **Entrega Especial** - 8 unidades - R$ 15,00

### 💰 Faturamento Total
- **R$ 42.000,00** (desde janeiro/2024)
- **Ticket médio**: R$ 77,78

---

## 🗃️ Estrutura do Banco (Nomes Reais das Tabelas/Colunas)

### Tabelas Principais:
- **pedido**: `id`, `id_pedido`, `preco_total`, `organizacao_id`, `usuario_criacao`, `cliente_id`, `status`, `data_criacao`, `data_alteracao`
- **item_pedido**: `id`, `pedido_id`, `produto_id`, `servico_id`, `quantidade`, `preco_unitario`, `preco_total`, `status`
- **produto**: `id`, `nome_produto`, `descricao`, `organizacao_id`, `status`, `data_criacao`
- **servico**: `id`, `nome_servico`, `descricao`, `organizacao_id`, `status`, `data_criacao`
- **cliente**: `id`, `nome_fantasia`, `organizacao_id`, `user_criacao`, `status`, `data_criacao`

### Status Relevantes:
- **Pedidos Faturados**: `status = 'CONCLUIDO'`
- **Registros Ativos**: `status != 'INATIVO'`

---

## 📋 DTOs Necessários

### 1. DTO Principal do Dashboard
```java
package com.mrbread.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardFaturamentoDTO {
    
    // Cards principais
    private VendasHojeDTO vendasHoje;
    private VendasMesDTO vendasMes;
    private ClientesAtivosDTO clientesAtivos;
    private ItensVendidosDTO itensVendidos;
    
    // Mais vendidos
    private List<MaisVendidoDTO> maisVendidos;
    
    // Faturamento total
    private FaturamentoTotalDTO faturamentoTotal;
}
```

### 2. DTOs dos Cards
```java
// Vendas de Hoje
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class VendasHojeDTO {
    private BigDecimal valor;           // R$ 150,00
    private Long quantidadePedidos;     // 5 pedidos
    private BigDecimal percentualVariacao; // +12%
}

// Vendas do Mês
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class VendasMesDTO {
    private BigDecimal valor;           // R$ 3.500,00
    private Long quantidadePedidos;     // 45 pedidos
    private BigDecimal percentualVariacao; // +8%
}

// Clientes Ativos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class ClientesAtivosDTO {
    private Long quantidade;            // 12
    private Long variacao;              // +3
}

// Itens Vendidos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class ItensVendidosDTO {
    private Long quantidade;            // 101
    private BigDecimal percentualVariacao; // +15%
}

// Mais Vendidos
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class MaisVendidoDTO {
    private String nome;                // "Pão Francês"
    private String tipo;                // "Produto" ou "Serviço"
    private Long quantidadeVendida;     // 45
    private BigDecimal valorTotal;      // R$ 22,50
}

// Faturamento Total
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public static class FaturamentoTotalDTO {
    private BigDecimal valorTotal;      // R$ 42.000,00
    private String periodoReferencia;   // "desde janeiro/2024"
    private BigDecimal ticketMedio;     // R$ 77,78
}
```

---

## 🔍 Queries para o Repository

### 1. Vendas de Hoje
```java
// No PedidoRepository.java
@Query(value = """
    SELECT 
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p 
    WHERE p.organizacao_id = :organizacaoId 
        AND p.status != 'INATIVO'
        AND p.data_criacao >= :inicioHoje
        AND p.data_criacao < :fimHoje
        AND (:userEmail IS NULL OR p.usuario_criacao = :userEmail)
    """, nativeQuery = true)
Object[] findVendasHoje(@Param("organizacaoId") UUID organizacaoId,
                        @Param("inicioHoje") LocalDateTime inicioHoje,
                        @Param("fimHoje") LocalDateTime fimHoje,
                        @Param("userEmail") String userEmail);

// Para calcular variação (ontem)
@Query(value = """
    SELECT 
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p 
    WHERE p.organizacao_id = :organizacaoId 
        AND p.status != 'INATIVO'
        AND p.data_criacao >= :inicioOntem
        AND p.data_criacao < :fimOntem
        AND (:userEmail IS NULL OR p.usuario_criacao = :userEmail)
    """, nativeQuery = true)
Object[] findVendasOntem(@Param("organizacaoId") UUID organizacaoId,
                         @Param("inicioOntem") LocalDateTime inicioOntem,
                         @Param("fimOntem") LocalDateTime fimOntem,
                         @Param("userEmail") String userEmail);
```

### 2. Vendas do Mês
```java
@Query(value = """
    SELECT 
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p 
    WHERE p.organizacao_id = :organizacaoId 
        AND p.status != 'INATIVO'
        AND p.data_criacao >= :inicioMes
        AND p.data_criacao < :fimMes
        AND (:userEmail IS NULL OR p.usuario_criacao = :userEmail)
    """, nativeQuery = true)
Object[] findVendasMesAtual(@Param("organizacaoId") UUID organizacaoId,
                            @Param("inicioMes") LocalDateTime inicioMes,
                            @Param("fimMes") LocalDateTime fimMes,
                            @Param("userEmail") String userEmail);

// Para calcular variação (mês anterior)
@Query(value = """
    SELECT 
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p 
    WHERE p.organizacao_id = :organizacaoId 
        AND p.status != 'INATIVO'
        AND p.data_criacao >= :inicioMesAnterior
        AND p.data_criacao < :fimMesAnterior
        AND (:userEmail IS NULL OR p.usuario_criacao = :userEmail)
    """, nativeQuery = true)
Object[] findVendasMesAnterior(@Param("organizacaoId") UUID organizacaoId,
                               @Param("inicioMesAnterior") LocalDateTime inicioMesAnterior,
                               @Param("fimMesAnterior") LocalDateTime fimMesAnterior,
                               @Param("userEmail") String userEmail);
```

### 3. Clientes Ativos (Cadastrados no Mês)
```java
// No ClienteRepository.java
@Query(value = """
    SELECT COUNT(c.id) as clientes_cadastrados
    FROM cliente c 
    WHERE c.organizacao_id = :organizacaoId 
        AND c.status != 'INATIVO'
        AND c.data_criacao >= :inicioMes
        AND c.data_criacao < :fimMes
        AND (:userEmail IS NULL OR c.user_criacao = :userEmail)
    """, nativeQuery = true)
Long findClientesCadastradosMesAtual(@Param("organizacaoId") UUID organizacaoId,
                                     @Param("inicioMes") LocalDateTime inicioMes,
                                     @Param("fimMes") LocalDateTime fimMes,
                                     @Param("userEmail") String userEmail);

// Mês anterior para variação
@Query(value = """
    SELECT COUNT(c.id) as clientes_cadastrados
    FROM cliente c 
    WHERE c.organizacao_id = :organizacaoId 
        AND c.status != 'INATIVO'
        AND c.data_criacao >= :inicioMesAnterior
        AND c.data_criacao < :fimMesAnterior
        AND (:userEmail IS NULL OR c.user_criacao = :userEmail)
    """, nativeQuery = true)
Long findClientesCadastradosMesAnterior(@Param("organizacaoId") UUID organizacaoId,
                                        @Param("inicioMesAnterior") LocalDateTime inicioMesAnterior,
                                        @Param("fimMesAnterior") LocalDateTime fimMesAnterior,
                                        @Param("userEmail") String userEmail);
```

### 4. Itens Vendidos no Mês
```java
// No PedidoRepository.java
@Query(value = """
    SELECT COUNT(ip.id) as total_itens_vendidos
    FROM item_pedido ip
    INNER JOIN pedido p ON ip.pedido_id = p.id
    WHERE p.organizacao_id = :organizacaoId
        AND p.status != 'INATIVO'
        AND ip.status != 'INATIVO'
        AND p.data_criacao >= :inicioMes
        AND p.data_criacao < :fimMes
        AND (:userEmail IS NULL OR p.usuario_criacao = :userEmail)
    """, nativeQuery = true)
Long findItensVendidosMesAtual(@Param("organizacaoId") UUID organizacaoId,
                               @Param("inicioMes") LocalDateTime inicioMes,
                               @Param("fimMes") LocalDateTime fimMes,
                               @Param("userEmail") String userEmail);

// Mês anterior para variação
@Query(value = """
    SELECT COUNT(ip.id) as total_itens_vendidos
    FROM item_pedido ip
    INNER JOIN pedido p ON ip.pedido_id = p.id
    WHERE p.organizacao_id = :organizacaoId
        AND p.status != 'INATIVO'
        AND ip.status != 'INATIVO'
        AND p.data_criacao >= :inicioMesAnterior
        AND p.data_criacao < :fimMesAnterior
        AND (:userEmail IS NULL OR p.usuario_criacao = :userEmail)
    """, nativeQuery = true)
Long findItensVendidosMesAnterior(@Param("organizacaoId") UUID organizacaoId,
                                  @Param("inicioMesAnterior") LocalDateTime inicioMesAnterior,
                                  @Param("fimMesAnterior") LocalDateTime fimMesAnterior,
                                  @Param("userEmail") String userEmail);
```

### 5. Top 3 Mais Vendidos
```java
@Query(value = """
    SELECT 
        CASE 
            WHEN ip.produto_id IS NOT NULL THEN prod.nome_produto
            WHEN ip.servico_id IS NOT NULL THEN serv.nome_servico
            ELSE 'Item Desconhecido'
        END as nome,
        CASE 
            WHEN ip.produto_id IS NOT NULL THEN 'Produto'
            WHEN ip.servico_id IS NOT NULL THEN 'Serviço'
            ELSE 'Desconhecido'
        END as tipo,
        SUM(ip.quantidade) as quantidade_total,
        SUM(ip.preco_total) as valor_total
    FROM item_pedido ip
    INNER JOIN pedido p ON ip.pedido_id = p.id
    LEFT JOIN produto prod ON ip.produto_id = prod.id
    LEFT JOIN servico serv ON ip.servico_id = serv.id
    WHERE p.organizacao_id = :organizacaoId
        AND p.status != 'INATIVO'
        AND ip.status != 'INATIVO'
        AND p.data_criacao >= :inicioMes
        AND p.data_criacao < :fimMes
        AND (:userEmail IS NULL OR p.usuario_criacao = :userEmail)
    GROUP BY 
        nome, tipo
    ORDER BY quantidade_total DESC
    LIMIT 3
    """, nativeQuery = true)
List<Object[]> findTop3MaisVendidos(@Param("organizacaoId") UUID organizacaoId,
                                    @Param("inicioMes") LocalDateTime inicioMes,
                                    @Param("fimMes") LocalDateTime fimMes,
                                    @Param("userEmail") String userEmail);
```

### 6. Faturamento Total (desde janeiro/2024)
```java
@Query(value = """
    SELECT 
        COUNT(p.id) as total_pedidos,
        COALESCE(SUM(p.preco_total), 0) as faturamento_total
    FROM pedido p 
    WHERE p.organizacao_id = :organizacaoId 
        AND p.status != 'INATIVO'
        AND p.data_criacao >= :dataInicio
        AND (:userEmail IS NULL OR p.usuario_criacao = :userEmail)
    """, nativeQuery = true)
Object[] findFaturamentoTotal(@Param("organizacaoId") UUID organizacaoId,
                              @Param("dataInicio") LocalDateTime dataInicio,
                              @Param("userEmail") String userEmail);
```

---

## 🎯 DashboardService Completo

### Implementação Completa do Serviço:
```java
package com.mrbread.service;

import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.repository.ClienteRepository;
import com.mrbread.domain.repository.PedidoRepository;
import com.mrbread.dto.DashboardFaturamentoDTO;
import com.mrbread.dto.DashboardFaturamentoDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardFaturamento", key = "#root.target.getCacheKey()", unless = "#result == null")
    public DashboardFaturamentoDTO obterDashboard() {
        log.info("Calculando métricas do dashboard de faturamento");
        
        UUID orgId = SecurityUtils.obterOrganizacaoId();
        if (orgId == null) {
            throw new IllegalStateException("Usuário sem organização não pode acessar métricas de faturamento");
        }
        
        String userEmail = SecurityUtils.isDefault() ? SecurityUtils.getEmail() : null;
        
        return DashboardFaturamentoDTO.builder()
                .vendasHoje(calcularVendasHoje(orgId, userEmail))
                .vendasMes(calcularVendasMes(orgId, userEmail))
                .clientesAtivos(calcularClientesAtivos(orgId, userEmail))
                .itensVendidos(calcularItensVendidos(orgId, userEmail))
                .maisVendidos(calcularMaisVendidos(orgId, userEmail))
                .faturamentoTotal(calcularFaturamentoTotal(orgId, userEmail))
                .build();
    }
    
    private VendasHojeDTO calcularVendasHoje(UUID orgId, String userEmail) {
        log.debug("Calculando vendas de hoje - OrgId: {}, UserEmail: {}", orgId, userEmail);
        
        // Calcular períodos para hoje
        LocalDateTime inicioHoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimHoje = inicioHoje.plusDays(1);
        
        // Calcular períodos para ontem
        LocalDateTime inicioOntem = inicioHoje.minusDays(1);
        LocalDateTime fimOntem = inicioHoje;
        
        Object[] hoje = pedidoRepository.findVendasHoje(orgId, inicioHoje, fimHoje, userEmail);
        Object[] ontem = pedidoRepository.findVendasOntem(orgId, inicioOntem, fimOntem, userEmail);
        
        Long quantidadeHoje = ((Number) hoje[0]).longValue();
        BigDecimal valorHoje = (BigDecimal) hoje[1];
        
        Long quantidadeOntem = ((Number) ontem[0]).longValue();
        BigDecimal valorOntem = (BigDecimal) ontem[1];
        
        BigDecimal variacao = calcularVariacaoPercentual(valorHoje, valorOntem);
        
        return VendasHojeDTO.builder()
                .valor(valorHoje)
                .quantidadePedidos(quantidadeHoje)
                .percentualVariacao(variacao)
                .build();
    }
    
    private VendasMesDTO calcularVendasMes(UUID orgId, String userEmail) {
        log.debug("Calculando vendas do mês - OrgId: {}, UserEmail: {}", orgId, userEmail);
        
        // Calcular períodos para mês atual
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);
        
        // Calcular períodos para mês anterior
        LocalDateTime inicioMesAnterior = inicioMesAtual.minusMonths(1);
        LocalDateTime fimMesAnterior = inicioMesAtual;
        
        Object[] mesAtual = pedidoRepository.findVendasMesAtual(orgId, inicioMesAtual, fimMesAtual, userEmail);
        Object[] mesAnterior = pedidoRepository.findVendasMesAnterior(orgId, inicioMesAnterior, fimMesAnterior, userEmail);
        
        Long quantidadeMesAtual = ((Number) mesAtual[0]).longValue();
        BigDecimal valorMesAtual = (BigDecimal) mesAtual[1];
        
        Long quantidadeMesAnterior = ((Number) mesAnterior[0]).longValue();
        BigDecimal valorMesAnterior = (BigDecimal) mesAnterior[1];
        
        BigDecimal variacao = calcularVariacaoPercentual(valorMesAtual, valorMesAnterior);
        
        return VendasMesDTO.builder()
                .valor(valorMesAtual)
                .quantidadePedidos(quantidadeMesAtual)
                .percentualVariacao(variacao)
                .build();
    }
    
    private ClientesAtivosDTO calcularClientesAtivos(UUID orgId, String userEmail) {
        log.debug("Calculando clientes ativos - OrgId: {}, UserEmail: {}", orgId, userEmail);
        
        // Calcular períodos para mês atual
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);
        
        // Calcular períodos para mês anterior
        LocalDateTime inicioMesAnterior = inicioMesAtual.minusMonths(1);
        LocalDateTime fimMesAnterior = inicioMesAtual;
        
        Long clientesMesAtual = clienteRepository.findClientesCadastradosMesAtual(orgId, inicioMesAtual, fimMesAtual, userEmail);
        Long clientesMesAnterior = clienteRepository.findClientesCadastradosMesAnterior(orgId, inicioMesAnterior, fimMesAnterior, userEmail);
        
        Long variacao = clientesMesAtual - clientesMesAnterior;
        
        return ClientesAtivosDTO.builder()
                .quantidade(clientesMesAtual)
                .variacao(variacao)
                .build();
    }
    
    private ItensVendidosDTO calcularItensVendidos(UUID orgId, String userEmail) {
        log.debug("Calculando itens vendidos - OrgId: {}, UserEmail: {}", orgId, userEmail);
        
        // Calcular períodos para mês atual
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);
        
        // Calcular períodos para mês anterior
        LocalDateTime inicioMesAnterior = inicioMesAtual.minusMonths(1);
        LocalDateTime fimMesAnterior = inicioMesAtual;
        
        Long itensMesAtual = pedidoRepository.findItensVendidosMesAtual(orgId, inicioMesAtual, fimMesAtual, userEmail);
        Long itensMesAnterior = pedidoRepository.findItensVendidosMesAnterior(orgId, inicioMesAnterior, fimMesAnterior, userEmail);
        
        BigDecimal variacao = calcularVariacaoPercentual(
                BigDecimal.valueOf(itensMesAtual), 
                BigDecimal.valueOf(itensMesAnterior)
        );
        
        return ItensVendidosDTO.builder()
                .quantidade(itensMesAtual)
                .percentualVariacao(variacao)
                .build();
    }
    
    private List<MaisVendidoDTO> calcularMaisVendidos(UUID orgId, String userEmail) {
        log.debug("Calculando mais vendidos - OrgId: {}, UserEmail: {}", orgId, userEmail);
        
        // Calcular períodos para mês atual
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);
        
        List<Object[]> results = pedidoRepository.findTop3MaisVendidos(orgId, inicioMesAtual, fimMesAtual, userEmail);
        List<MaisVendidoDTO> maisVendidos = new ArrayList<>();
        
        for (Object[] result : results) {
            maisVendidos.add(MaisVendidoDTO.builder()
                    .nome((String) result[0])
                    .tipo((String) result[1])
                    .quantidadeVendida(((Number) result[2]).longValue())
                    .valorTotal((BigDecimal) result[3])
                    .build());
        }
        
        return maisVendidos;
    }
    
    private FaturamentoTotalDTO calcularFaturamentoTotal(UUID orgId, String userEmail) {
        log.debug("Calculando faturamento total - OrgId: {}, UserEmail: {}", orgId, userEmail);
        
        // Desde janeiro/2024
        LocalDateTime dataInicio = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        Object[] result = pedidoRepository.findFaturamentoTotal(orgId, dataInicio, userEmail);
        
        Long totalPedidos = ((Number) result[0]).longValue();
        BigDecimal faturamentoTotal = (BigDecimal) result[1];
        
        // Calcular ticket médio
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
    
    /**
     * Calcula a variação percentual entre dois valores
     * @param valorAtual Valor atual
     * @param valorAnterior Valor anterior para comparação
     * @return Percentual de variação (positivo = crescimento, negativo = queda)
     */
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
    
    /**
     * Gera chave de cache baseada no contexto do usuário
     */
    public String getCacheKey() {
        UUID orgId = SecurityUtils.obterOrganizacaoId();
        String userEmail = SecurityUtils.isDefault() ? SecurityUtils.getEmail() : "all";
        return orgId + "_" + userEmail;
    }
}
```

### Controller para Exposição da API:
```java
package com.mrbread.rest;

import com.mrbread.dto.DashboardFaturamentoDTO;
import com.mrbread.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/faturamento")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DashboardFaturamentoDTO> obterDadosFaturamento() {
        log.info("Requisição para obter dados de faturamento do dashboard");
        
        try {
            DashboardFaturamentoDTO dados = dashboardService.obterDashboard();
            return ResponseEntity.ok(dados);
        } catch (IllegalStateException e) {
            log.warn("Usuário sem organização tentou acessar dashboard: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao obter dados de faturamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
```

---

## 📅 Como Calcular os Períodos no Service

### Exemplo de Cálculo de Períodos com LocalDateTime:
```java
// Para HOJE (00:00:00 até 23:59:59)
LocalDateTime inicioHoje = LocalDateTime.now()
    .withHour(0).withMinute(0).withSecond(0).withNano(0);
LocalDateTime fimHoje = inicioHoje.plusDays(1);

// Para ONTEM
LocalDateTime inicioOntem = inicioHoje.minusDays(1);
LocalDateTime fimOntem = inicioHoje;

// Para MÊS ATUAL (dia 1 00:00:00 até último dia 23:59:59)
LocalDateTime inicioMesAtual = LocalDateTime.now()
    .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);

// Para MÊS ANTERIOR
LocalDateTime inicioMesAnterior = inicioMesAtual.minusMonths(1);
LocalDateTime fimMesAnterior = inicioMesAtual;
```

### ⚡ Vantagens dessa Abordagem:
- ✅ **Database Agnostic**: Funciona em qualquer banco
- ✅ **Testável**: Pode mockar datas específicas nos testes
- ✅ **Preciso**: Range exato sem depender de funções SQL
- ✅ **Performance**: Usa índices de data eficientemente
- ✅ **Timezone Safe**: Usa timezone da aplicação

---

## 🚀 Controle de Acesso

### Lógica de Permissões:
- **DEFAULT**: Vê apenas seus próprios dados (`userEmail != null`)
- **MANAGER/ADMIN**: Vê dados de toda a organização (`userEmail = null`)

### Implementação:
```java
String userEmail = SecurityUtils.isDefault() ? SecurityUtils.getEmail() : null;
```

---

## 📝 Observações Importantes

1. **Status de Pedidos**: Use `'CONCLUIDO'` para pedidos faturados
2. **Nomes das Colunas**: Use os nomes reais do banco (`nome_produto`, `nome_servico`, etc.)
3. **Variações Percentuais**: Calcule comparando com período anterior
4. **Cache**: Considere usar `@Cacheable` nas queries mais pesadas
5. **Índices**: Crie índices em `data_criacao`, `organizacao_id`, `status` para performance

---

**Criado em:** Janeiro 2025  
**Versão:** 1.0  
**Status:** Pronto para implementação
