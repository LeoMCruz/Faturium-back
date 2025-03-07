package com.mrbread.domain.repository;

import com.mrbread.domain.model.Cliente;
import com.mrbread.domain.model.Produto;
import com.mrbread.domain.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends PertenceOrganizacaoRespository<Cliente, UUID> {
}
