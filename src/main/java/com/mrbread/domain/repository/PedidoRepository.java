package com.mrbread.domain.repository;

import com.mrbread.domain.model.Pedido;
import com.mrbread.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    Page<Pedido> findByOrganizacaoIdOrgAndStatus(UUID organizacaoId, Status status, Pageable pageable);
}
