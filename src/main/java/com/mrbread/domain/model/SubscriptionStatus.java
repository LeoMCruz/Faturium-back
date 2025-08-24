package com.mrbread.domain.model;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    ACTIVE("Ativa"),
    EXPIRED("Expirada"),
    CANCELLED("Cancelada"),
    SUSPENDED("Suspensa"),
    PENDING("Pendente"),
    TRIAL("Período de teste");
    
    private final String description;
    
    SubscriptionStatus(String description) {
        this.description = description;
    }

}
