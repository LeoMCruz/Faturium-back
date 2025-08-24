package com.mrbread.domain.repository;

import com.mrbread.domain.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends PertenceOrganizacaoRespository<Pedido, UUID> {
    @Query("SELECT MAX(p.idPedido) FROM Pedido p where p.organizacao.idOrg = :organizacaoId")
    Long findMaxIdPedido(UUID organizacaoId);

    @Query("select e from Pedido e where e.organizacao.idOrg = :organizacaoId and e.status != com.mrbread.domain.model.Status.INATIVO and e.user.login = :username")
    Page<Pedido> findAllByUser(UUID organizacaoId, Pageable pageable, String username);

    //VENDAS DIARIAS
    @Query(value = """
    SELECT
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p
    WHERE p.organizacao_id = :organizacaoId
        AND p.status = 2
        AND p.data_criacao >= :inicioHoje
                        AND p.data_criacao < :fimHoje
        AND (:username IS NULL OR p.usuario_criacao = :username)
    """, nativeQuery = true)
    Object[] findVendasHoje(UUID organizacaoId, LocalDateTime inicioHoje, LocalDateTime fimHoje, String username);

    @Query(value = """
    SELECT
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p
    WHERE p.organizacao_id = :organizacaoId
        AND p.status = 2
        AND p.data_criacao >= :inicioOntem
                        AND p.data_criacao < :fimOntem
        AND (:username IS NULL OR p.usuario_criacao = :username)
    """, nativeQuery = true)
    Object[] findVendasOntem(UUID organizacaoId,LocalDateTime inicioOntem, LocalDateTime fimOntem, String username);

    //#################################################################################

    //VENDAS MENSAIS
    @Query(value = """
    SELECT
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p
    WHERE p.organizacao_id = :organizacaoId
        AND p.status = 2
        AND p.data_criacao >= :inicioMes
                        AND p.data_criacao < :fimMes
        AND (:username IS NULL OR p.usuario_criacao = :username)
    """, nativeQuery = true)
    Object[] findVendasMesAtual(UUID organizacaoId, LocalDateTime inicioMes, LocalDateTime fimMes, String username);

    @Query(value = """
    SELECT
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p
    WHERE p.organizacao_id = :organizacaoId
        AND p.status = 2
        AND p.data_criacao >= :inicioMes
                        AND p.data_criacao < :fimMes
    """, nativeQuery = true)
    Object[] findVendasMesAtualOrg(UUID organizacaoId, LocalDateTime inicioMes, LocalDateTime fimMes);

    @Query(value = """
    SELECT
        COUNT(p.id) as quantidade_pedidos,
        COALESCE(SUM(p.preco_total), 0) as valor_total
    FROM pedido p
    WHERE p.organizacao_id = :organizacaoId
        AND p.status = 2
        AND p.data_criacao >= :inicioMesAnterior
                        AND p.data_criacao < :fimMesAnterior
        AND (:username IS NULL OR p.usuario_criacao = :username)
    """, nativeQuery = true)
    Object[] findVendasMesAnterior(UUID organizacaoId, LocalDateTime inicioMesAnterior, LocalDateTime fimMesAnterior, String username);

    //#################################################################################

    //TOTAL ITENS VENDIDO MES
    @Query(value = """
    SELECT COUNT(ip.id) as total_itens_vendidos
    FROM item_pedido ip
    INNER JOIN pedido p ON ip.pedido_id = p.id
    WHERE p.organizacao_id = :organizacaoId
        AND p.status = 2
        AND ip.status != 1
        AND p.data_criacao >= :inicioMes
        AND p.data_criacao < :fimMes
        AND (:username IS NULL OR p.usuario_criacao = :username)
    """, nativeQuery = true)
    Long findItensVendidosMesAtual(UUID organizacaoId, LocalDateTime inicioMes, LocalDateTime fimMes, String username);

    @Query(value = """
    SELECT COUNT(ip.id) as total_itens_vendidos
    FROM item_pedido ip
    INNER JOIN pedido p ON ip.pedido_id = p.id
    WHERE p.organizacao_id = :organizacaoId
        AND p.status = 2
        AND ip.status != 1
        AND p.data_criacao >= :inicioMesAnterior
        AND p.data_criacao < :fimMesAnterior
        AND (:username IS NULL OR p.usuario_criacao = :username)
    """, nativeQuery = true)
    Long findItensVendidosMesAnterior(UUID organizacaoId, LocalDateTime inicioMesAnterior, LocalDateTime fimMesAnterior, String username);

    //#################################################################################

    // TOP 5 ITENS
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
        AND p.status = 2
        AND ip.status != 1
        AND p.data_criacao >= :inicioMes
        AND p.data_criacao < :fimMes
        AND (:username IS NULL OR p.usuario_criacao = :username)
    GROUP BY
        nome, tipo
    ORDER BY quantidade_total DESC
    LIMIT 3
    """, nativeQuery = true)
    List<Object[]> findTop3MaisVendidos(UUID organizacaoId, LocalDateTime inicioMes, LocalDateTime fimMes, String username);

    // FATURAMENTO ANUAL
    @Query(value = """
    SELECT
        COUNT(p.id) as total_pedidos,
        COALESCE(SUM(p.preco_total), 0) as faturamento_total
    FROM pedido p
    WHERE p.organizacao_id = :organizacaoId
        AND p.status = 2
        AND p.data_criacao >= :dataInicio
        AND (:username IS NULL OR p.usuario_criacao = :username)
    """, nativeQuery = true)
    Object[] findFaturamentoTotal(UUID organizacaoId, LocalDateTime dataInicio, String username);


}
