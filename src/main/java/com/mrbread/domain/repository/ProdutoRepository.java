package com.mrbread.domain.repository;

import com.mrbread.domain.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByOrganizacaoIdOrg(UUID organizacaoId, Pageable pageable);
}
