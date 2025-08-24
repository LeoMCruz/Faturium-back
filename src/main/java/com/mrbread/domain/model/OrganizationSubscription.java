package com.mrbread.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizationSubscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idOrg", nullable = false)
    private Organizacao organization;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status; // ACTIVE, EXPIRED, CANCELLED, SUSPENDED
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @Column
    private Boolean autoRenew = true;
    
    @Column
    private String paymentMethod; // "PIX", "CREDIT_CARD", etc.
    
    @Column
    private LocalDateTime lastPaymentDate;
    
    @Column
    private LocalDateTime nextPaymentDate;
    
    @Column
    private LocalDateTime cancellationDate;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Metodo para verificar se a assinatura está ativa
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE && 
               LocalDateTime.now().isBefore(endDate);
    }
    
    // Metodo para verificar se está próxima de expirar
    public boolean isExpiringSoon() {
        return LocalDateTime.now().isAfter(endDate.minusDays(7));
    }
}
