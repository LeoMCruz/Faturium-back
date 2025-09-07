package com.faturium.service;

import com.faturium.config.exception.AppException;
import com.faturium.config.security.SecurityUtils;
import com.faturium.domain.model.*;
import com.faturium.domain.repository.*;
import com.faturium.dto.SubscriptionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationSubscriptionService {

    private final OrganizationSubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;

    public void createSubscription(Payment payment, Plan plan) {
        var organization = organizacaoRepository.findById(payment.getIdOrg())
                .orElseThrow(() -> new AppException("Organização não encontrada", "ID inválido", HttpStatus.NOT_FOUND));

        var currentSub = getCurrentSubscriptionEntity(payment.getIdOrg());

        if(currentSub.getPlan().getId().equals(plan.getId())){
            extendSubscription(plan, currentSub);
        }

        cancelCurrentSubscription(payment.getIdOrg());

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan.getBillingCycle());

        OrganizationSubscription subscription = OrganizationSubscription.builder()
                .organization(organization)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .endDate(endDate)
                .autoRenew(true)
                .paymentMethod("PIX")
                .build();

        OrganizationSubscription savedSubscription = subscriptionRepository.save(subscription);
        convertToDTO(savedSubscription);
    }

    public SubscriptionDTO getCurrentSubscription() {
        OrganizationSubscription subscription = getCurrentSubscriptionEntity(SecurityUtils.obterOrganizacaoId());
        return subscription != null ? convertToDTO(subscription) : null;
    }

    public List<SubscriptionDTO> getOrganizationSubscriptions() {
        return subscriptionRepository.findByOrganizationIdOrg(SecurityUtils.obterOrganizacaoId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void cancelSubscription(String reason) {
        OrganizationSubscription subscription = getCurrentSubscriptionEntity(SecurityUtils.obterOrganizacaoId());
        if (subscription != null) {
            subscription.setStatus(SubscriptionStatus.CANCELLED);
            subscription.setCancellationDate(LocalDateTime.now());
            subscription.setCancellationReason(reason);
            subscriptionRepository.save(subscription);
        }
    }

    // public void renewSubscription(UUID organizationId) {
    // OrganizationSubscription subscription =
    // getCurrentSubscriptionEntity(organizationId);
    // if (subscription != null && subscription.getAutoRenew()) {
    // // Implementar lógica de renovação automática
    // // Pode incluir cobrança automática via PIX
    // }
    // }

    public boolean canOrganizationCreateOrders(UUID organizationId) {
        OrganizationSubscription subscription = getCurrentSubscriptionEntity(organizationId);
        System.out.println(subscription);
        if (subscription == null)
            return false;

        // Verificar limite de pedidos do mês
        return getOrdersThisMonth(organizationId) < subscription.getPlan().getMaxOrdersPerMonth();
    }

    public boolean canOrganizationAddUser(UUID organizationId) {
        OrganizationSubscription subscription = getCurrentSubscriptionEntity(organizationId);
        if (subscription == null)
            return false;

        // Verificar limite de usuários
        return getCurrentUserCount(organizationId) < subscription.getPlan().getMaxUsers();
    }

    @Scheduled(cron = "0 0 1 * * ?") // Executar tod dia 1h
    public void checkExpiredSubscriptions() {
        List<OrganizationSubscription> expiringSubscriptions = subscriptionRepository
                .findByStatusAndEndDateBefore(SubscriptionStatus.ACTIVE, LocalDateTime.now());

        for (OrganizationSubscription subscription : expiringSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(subscription);

            var activeSubscriptions = subscriptionRepository.findActiveByOrganizationIdOrg(subscription.getOrganization().getIdOrg(), SubscriptionStatus.ACTIVE);
            if(activeSubscriptions.isEmpty()){
                createDefaultSubscription(subscription.getOrganization().getIdOrg());
            }
        }
    }

    private LocalDateTime calculateEndDate(LocalDateTime startDate, BillingCycle billingCycle) {
        return switch (billingCycle) {
            case YEARLY -> startDate.plusYears(1);
            case LIFETIME -> startDate.plusYears(100);
            default -> startDate.plusMonths(1);
        };
    }

    private OrganizationSubscription getCurrentSubscriptionEntity(UUID organizationId) {
        return subscriptionRepository.findByOrganizationIdOrgAndStatus(organizationId, SubscriptionStatus.ACTIVE);
    }

    private void cancelCurrentSubscription(UUID idOrg) {
        OrganizationSubscription current = getCurrentSubscriptionEntity(idOrg);
        if (current != null) {
            current.setStatus(SubscriptionStatus.CANCELLED);
            current.setCancellationDate(LocalDateTime.now());
            subscriptionRepository.save(current);
        }
    }

    private Long getOrdersThisMonth(UUID organizationId) {
        LocalDateTime inicioMesAtual = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                .withNano(0);
        LocalDateTime fimMesAtual = inicioMesAtual.plusMonths(1);
        Object[] mesAtual = pedidoRepository.findVendasMesAtualOrg(organizationId, inicioMesAtual, fimMesAtual);
        Object[] dadosMesAtual = (Object[]) mesAtual[0];
        Long quantidadeMesAtual = ((Number) dadosMesAtual[0]).longValue();
        System.out.println("orders"+ quantidadeMesAtual);
        return quantidadeMesAtual;
    }

    private Long getCurrentUserCount(UUID organizationId) {
        return userRepository.countActiveUsersByOrganization(organizationId);
    }

    private SubscriptionDTO convertToDTO(OrganizationSubscription subscription) {
        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .organizationId(subscription.getOrganization().getIdOrg())
                .organizationName(subscription.getOrganization().getNomeOrganizacao())
                .planId(subscription.getPlan().getId())
                .planName(subscription.getPlan().getName())
                .status(subscription.getStatus().name())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.getAutoRenew())
                .paymentMethod(subscription.getPaymentMethod())
                .lastPaymentDate(subscription.getLastPaymentDate())
                .nextPaymentDate(subscription.getNextPaymentDate())
                .cancellationDate(subscription.getCancellationDate())
                .cancellationReason(subscription.getCancellationReason())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }

    public void createDefaultSubscription (UUID idOrg){
        var organization = organizacaoRepository.findByIdOrg(idOrg)
                .orElseThrow(() -> new AppException("Organização não encontrada", "ID inválido", HttpStatus.NOT_FOUND));

        var freePlan = planRepository.findByName("Free").orElseThrow(() -> new AppException("Plano não encontrado", "ID inválido", HttpStatus.NOT_FOUND));

        LocalDateTime startDate = LocalDateTime.now();

        OrganizationSubscription subscription = OrganizationSubscription.builder()
                .organization(organization)
                .plan(freePlan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(startDate)
                .endDate(calculateEndDate(LocalDateTime.now(), freePlan.getBillingCycle()))
                .autoRenew(true)
                .paymentMethod("GRATUITO")
                .build();

        subscriptionRepository.save(subscription);
    }

    private void extendSubscription(Plan plan, OrganizationSubscription currentSub){

        LocalDateTime endDate = calculateEndDate(currentSub.getEndDate(), plan.getBillingCycle());

        OrganizationSubscription subscription = OrganizationSubscription.builder()
                .organization(currentSub.getOrganization())
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(currentSub.getEndDate())
                .endDate(endDate)
                .autoRenew(true)
                .paymentMethod("PIX")
                .build();

        OrganizationSubscription savedSubscription = subscriptionRepository.save(subscription);
        convertToDTO(savedSubscription);
    }
}
