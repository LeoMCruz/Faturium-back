package com.faturium.domain.repository;

import com.faturium.domain.model.User;

import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PertenceOrganizacaoRespository<User, UUID> {
    Optional<User> findByLogin(String login);

    Optional<User> findByLoginAndOrganizacaoIdOrg(String login, UUID idOrg);

    boolean existsByLogin(String login);

    // Conta usuários ativos de uma organização
    @Query("SELECT COUNT(u) FROM User u WHERE u.organizacao.idOrg = :idOrg AND u.status = com.faturium.domain.model.Status.ATIVO")
    Long countActiveUsersByOrganization(UUID idOrg);

    // Conta todos os usuários de uma organização (independente do status)
    @Query("SELECT COUNT(u) FROM User u WHERE u.organizacao.idOrg = :idOrg")
    Long countUsersByOrganization(UUID idOrg);
}
