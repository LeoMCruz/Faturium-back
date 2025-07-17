package com.mrbread.domain.repository;

import com.mrbread.domain.model.Produto;
import com.mrbread.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository extends PertenceOrganizacaoRespository<Produto, UUID> {
    @Query("select e from Produto e where " +
            "e.organizacao.idOrg = :organizacaoId and e.status = com.mrbread.domain.model.Status.ATIVO " +
            " and e.nomeProduto like :search")
    List<Produto> findByName(UUID organizacaoId, String search);
}
