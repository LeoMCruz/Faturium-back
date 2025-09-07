package com.faturium.service;

import com.faturium.config.exception.AppException;
import com.faturium.domain.model.BillingCycle;
import com.faturium.domain.model.Plan;
import com.faturium.domain.repository.PlanRepository;
import com.faturium.dto.PlanDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

    @Transactional
    public List<PlanDTO> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanDTO getPlanById(UUID id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new AppException("Plano não encontrado", "ID inválido", HttpStatus.NOT_FOUND));
        return convertToDTO(plan);
    }

    @Transactional
    public PlanDTO createPlan(PlanDTO planDTO) {
        if(planRepository.findByName(planDTO.getName()).isPresent()){
            throw new AppException("Plano ja existe",  "Ja existe um plano com esse nome", HttpStatus.CONFLICT);
        }
        validatePlanLimits(planDTO);

        Plan plan = Plan.builder()
                .name(planDTO.getName())
                .description(planDTO.getDescription())
                .price(planDTO.getPrice())
                .billingCycle(BillingCycle.valueOf(planDTO.getBillingCycle().toUpperCase()))
                .maxUsers(planDTO.getMaxUsers())
                .maxOrdersPerMonth(planDTO.getMaxOrdersPerMonth())
                .isActive(true)
                .build();

        Plan savedPlan = planRepository.save(plan);
        return convertToDTO(savedPlan);
    }

//    public PlanDTO updatePlan(UUID id, PlanDTO planDTO) {
//        Plan plan = planRepository.findById(id)
//                .orElseThrow(() -> new AppException("Plano não encontrado", "ID inválido", HttpStatus.NOT_FOUND));
//
//        if (planDTO.getName() != null && !planDTO.getName().equals(plan.getName())) {
//            validatePlanName(planDTO.getName(), id);
//        }
//
//        if (planDTO.getName() != null) plan.setName(planDTO.getName());
//        if (planDTO.getDescription() != null) plan.setDescription(planDTO.getDescription());
//        if (planDTO.getPrice() != null) plan.setPrice(planDTO.getPrice());
//        if (planDTO.getBillingCycle() != null) plan.setBillingCycle(BillingCycle.valueOf(planDTO.getBillingCycle().toUpperCase()));
//        if (planDTO.getMaxUsers() != null) plan.setMaxUsers(planDTO.getMaxUsers());
//        if (planDTO.getMaxOrdersPerMonth() != null) plan.setMaxOrdersPerMonth(planDTO.getMaxOrdersPerMonth());
//        if (planDTO.getIsActive() != null) plan.setIsActive(planDTO.getIsActive());
//
//        Plan updatedPlan = planRepository.save(plan);
//        return convertToDTO(updatedPlan);
//    }

//    public void deletePlan(UUID id) {
//        Plan plan = planRepository.findById(id)
//                .orElseThrow(() -> new AppException("Plano não encontrado", "ID inválido", HttpStatus.NOT_FOUND));
//
//        plan.setIsActive(false);
//        planRepository.save(plan);
//    }

//    public List<PlanDTO> getActivePlans() {
//        return planRepository.findByIsActiveTrue().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }

//    public List<PlanDTO> getPlansByBillingCycle(String billingCycle) {
//        return planRepository.findActiveByBillingCycle(billingCycle).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }

//    public PlanDTO getDefaultPlan() {
//        Plan defaultPlan = planRepository.findByName("FREE")
//                .orElse(Plan.builder()
//                        .name("FREE")
//                        .description("Plano gratuito padrão")
//                        .price(BigDecimal.ZERO)
//                        .billingCycle(BillingCycle.MONTHLY)
//                        .maxUsers(1)
//                        .maxOrdersPerMonth(10)
//                        .isActive(true)
//                        .build());
//
//        return convertToDTO(defaultPlan);
//    }

    @Transactional
    private void validatePlanLimits(PlanDTO planDTO) {
        if (planDTO.getMaxUsers() != null && planDTO.getMaxUsers() < 1) {
            throw new AppException("Limite inválido", "Máximo de usuários deve ser pelo menos 1", HttpStatus.BAD_REQUEST);
        }

        if (planDTO.getMaxOrdersPerMonth() != null && planDTO.getMaxOrdersPerMonth() < 0) {
            throw new AppException("Limite inválido", "Máximo de pagamentos não pode ser negativo", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    private PlanDTO convertToDTO(Plan plan) {
        return PlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .billingCycle(plan.getBillingCycle() != null ? plan.getBillingCycle().name() : null)
                .maxUsers(plan.getMaxUsers())
                .maxOrdersPerMonth(plan.getMaxOrdersPerMonth())
                .isActive(plan.getIsActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}
