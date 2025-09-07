//package com.faturium.dto;
//
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.UUID;
//
//@Builder
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class CreateSubscriptionRequest {
//
//    @NotNull(message = "ID do plano é obrigatório")
//    private UUID planId;
//
//    @NotNull(message = "Ciclo de cobrança é obrigatório")
//    private String billingCycle;
//
//    private String paymentMethod;
//}
