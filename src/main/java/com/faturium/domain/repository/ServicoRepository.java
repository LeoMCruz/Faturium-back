package com.faturium.domain.repository;

import com.faturium.domain.model.Servico;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ServicoRepository extends PertenceOrganizacaoRespository<Servico, UUID> {
    @Query("select e from Servico e where " +
            "e.organizacao.idOrg = :organizacaoId and e.status = com.faturium.domain.model.Status.ATIVO " +
            " and e.nomeServico like :search")
    List<Servico> findByName(UUID organizacaoId, String search, Pageable pageable);
}
