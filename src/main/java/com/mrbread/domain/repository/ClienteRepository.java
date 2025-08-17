package com.mrbread.domain.repository;

import com.mrbread.domain.model.Cliente;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClienteRepository extends PertenceOrganizacaoRespository<Cliente, UUID> {
    @Query("select e from Cliente e where " +
            "e.organizacao.idOrg = :organizacaoId and e.status = com.mrbread.domain.model.Status.ATIVO " +
            " and e.nomeFantasia like :search")
    List<Cliente> findByName(UUID organizacaoId, String search, Pageable pageable);

    @Query(value = """
    SELECT COUNT(c.id) as clientes_cadastrados
    FROM cliente c
    WHERE c.organizacao_id = :organizacaoId
        AND c.status = 2
        AND c.data_criacao >= :inicioMes
        AND c.data_criacao < :fimMes
        AND (:username IS NULL OR c.user_criacao = :username)
    """, nativeQuery = true)
    Long findClientesCadastradosMesAtual(UUID organizacaoId, LocalDateTime inicioMes, LocalDateTime fimMes, String username);

    // Mês anterior para variação
    @Query(value = """
    SELECT COUNT(c.id) as clientes_cadastrados
    FROM cliente c
    WHERE c.organizacao_id = :organizacaoId
        AND c.status = 2
        AND c.data_criacao >= :inicioMesAnterior
        AND c.data_criacao < :fimMesAnterior
        AND (:username IS NULL OR c.user_criacao = :username)
    """, nativeQuery = true)
    Long findClientesCadastradosMesAnterior(UUID organizacaoId, LocalDateTime inicioMesAnterior, LocalDateTime fimMesAnterior, String username);
}
