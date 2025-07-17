package com.mrbread.domain.repository;

import com.mrbread.domain.model.Cliente;
import com.mrbread.domain.model.Produto;
import com.mrbread.domain.model.Servico;
import com.mrbread.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends PertenceOrganizacaoRespository<Cliente, UUID> {
    @Query("select e from Cliente e where " +
            "e.organizacao.idOrg = :organizacaoId and e.status = com.mrbread.domain.model.Status.ATIVO " +
            " and e.nomeFantasia like :search")
    List<Servico> findByName(UUID organizacaoId, String search);
}
