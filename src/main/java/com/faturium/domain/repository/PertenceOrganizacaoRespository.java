package com.faturium.domain.repository;

import com.faturium.domain.model.PertenceOrganizacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface PertenceOrganizacaoRespository<T extends PertenceOrganizacao, TID> extends JpaRepository<T, TID> {
    @Query("select e from #{#entityName} e where e.id = :id and e.organizacao.idOrg = :organizacaoId")
    Optional<T> findById(UUID id, UUID organizacaoId);

    @Query("select e from #{#entityName} e where e.organizacao.idOrg = :organizacaoId and e.status != com.faturium.domain.model.Status.INATIVO")
    Page<T> findAll(UUID organizacaoId, Pageable pageable);
}
