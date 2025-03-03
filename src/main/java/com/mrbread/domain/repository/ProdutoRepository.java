package com.mrbread.domain.repository;

import com.mrbread.domain.model.Produto;
import com.mrbread.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    Page<Produto> findByOrganizacaoIdOrgAndStatus(UUID organizacaoId, Status status, Pageable pageable);
}
