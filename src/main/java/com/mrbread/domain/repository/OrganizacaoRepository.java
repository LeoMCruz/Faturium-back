package com.mrbread.domain.repository;

import com.mrbread.domain.model.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {
    boolean existsByCnpj(String cnpj);
    Optional<Organizacao> findByIdOrg(UUID idOrg);
    boolean existsByIdOrg (UUID idOrg);
}
