package com.mrbread.domain.repository;

import com.mrbread.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
    List<User> findByOrganizacaoIdOrg(UUID organizacaoIdOrg);
}
