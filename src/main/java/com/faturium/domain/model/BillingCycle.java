package com.faturium.domain.model;

import lombok.Getter;

@Getter
public enum BillingCycle {
    MONTHLY("Mensal"),
    YEARLY("Anual"),
    LIFETIME("Vitalício");
    
    private final String description;
    
    BillingCycle(String description) {
        this.description = description;
    }

}
