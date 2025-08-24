package com.mrbread.domain.repository;

import com.mrbread.domain.model.OrganizationSubscription;
import com.mrbread.domain.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationSubscriptionRepository extends JpaRepository<OrganizationSubscription, UUID> {

    OrganizationSubscription findByOrganizationIdOrgAndStatus(UUID organizationId, SubscriptionStatus status);

    List<OrganizationSubscription> findByStatusAndEndDateBefore(SubscriptionStatus status, LocalDateTime date);

    List<OrganizationSubscription> findByOrganizationIdOrg(UUID organizationId);

    @Query("SELECT os FROM OrganizationSubscription os WHERE os.organization.idOrg = :orgId AND os.status = 'ACTIVE'")
    Optional<OrganizationSubscription> findActiveByOrganizationIdOrg(@Param("orgId") UUID orgId,
            @Param("status") SubscriptionStatus status);

    @Query("SELECT os FROM OrganizationSubscription os WHERE os.status = 'ACTIVE' AND os.endDate BETWEEN :startDate AND :endDate")
    List<OrganizationSubscription> findExpiringBetween(LocalDateTime startDate, LocalDateTime endDate);
}
