package com.mrbread.rest;

import com.mrbread.dto.SubscriptionDTO;
import com.mrbread.service.OrganizationSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final OrganizationSubscriptionService subscriptionService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/subscriptions/current")
    public ResponseEntity<SubscriptionDTO> getCurrentSubscription() {
        SubscriptionDTO subscription = subscriptionService.getCurrentSubscription();
        return subscription != null ? ResponseEntity.ok(subscription) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping("/subscriptions/organization")
    public ResponseEntity<List<SubscriptionDTO>> getOrganizationSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getOrganizationSubscriptions());
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @PostMapping("/subscriptions/cancel")
    public ResponseEntity<Void> cancelSubscription(@RequestParam String reason) {
        subscriptionService.cancelSubscription(reason);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/subscriptions/admin/renew/{subscriptionId}")
    public ResponseEntity<SubscriptionDTO> renewSubscription(@PathVariable Long subscriptionId) {
        // Implementar metodo para renovar assinatura específica
        return ResponseEntity.ok(null);
    }
}
