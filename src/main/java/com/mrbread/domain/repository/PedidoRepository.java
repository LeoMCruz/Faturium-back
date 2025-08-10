package com.mrbread.domain.repository;

import com.mrbread.domain.model.Cliente;
import com.mrbread.domain.model.Pedido;
import com.mrbread.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PedidoRepository extends PertenceOrganizacaoRespository<Pedido, UUID> {
    @Query("SELECT MAX(p.idPedido) FROM Pedido p")
    Long findMaxIdPedido();

    @Query("select e from Pedido e where e.organizacao.idOrg = :organizacaoId and e.status != com.mrbread.domain.model.Status.INATIVO and e.user.login = :username")
    Page<Pedido> findAllByUser(UUID organizacaoId, Pageable pageable, String username);
}
