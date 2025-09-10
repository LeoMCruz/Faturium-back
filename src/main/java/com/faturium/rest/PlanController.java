package com.faturium.rest;

import com.faturium.dto.PlanDTO;
import com.faturium.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/plans")
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/plans/{id}")
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

//    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
//    @GetMapping("/plans/active")
//    public ResponseEntity<List<PlanDTO>> getActivePlans() {
//        return ResponseEntity.ok(planService.getActivePlans());
//    }
//
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PostMapping("/plans")
//    public ResponseEntity<PlanDTO> createPlan(@RequestBody PlanDTO planDTO) {
//        return ResponseEntity.ok(planService.createPlan(planDTO));
//    }
//
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PutMapping("/plans/{id}")
//    public ResponseEntity<PlanDTO> updatePlan(@PathVariable UUID id, @RequestBody PlanDTO planDTO) {
//        return ResponseEntity.ok(planService.updatePlan(id, planDTO));
//    }
//
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @DeleteMapping("/plans/{id}")
//    public ResponseEntity<Void> deletePlan(@PathVariable UUID id) {
//        planService.deletePlan(id);
//        return ResponseEntity.noContent().build();
//    }
}
