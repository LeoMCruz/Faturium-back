package com.faturium.domain.repository;

import com.faturium.domain.model.Produto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends PertenceOrganizacaoRespository<Produto, UUID> {
    @Query("select e from Produto e where " +
            "e.organizacao.idOrg = :organizacaoId and e.status = com.faturium.domain.model.Status.ATIVO " +
            " and e.nomeProduto like :search")
    List<Produto> findByName(UUID organizacaoId, String search, Pageable pageable);
}
