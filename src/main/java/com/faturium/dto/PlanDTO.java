package com.faturium.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {

    // Campo gerado automaticamente - ignorado na criação
    private UUID id;

    @NotBlank(message = "Nome do plano é obrigatório")
    @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    private String name;

    @Size(max = 500, message = "Descrição muito longa")
    private String description;

    @DecimalMin(value = "0.00", message = "Preço não pode ser negativo")
    private BigDecimal price;

    @NotBlank(message = "Ciclo de cobrança é obrigatório")
    private String billingCycle;

    @Min(value = 1, message = "Máximo de usuários deve ser pelo menos 1")
    private Integer maxUsers;

    @Min(value = 0, message = "Máximo de pagamentos não pode ser negativo")
    private Integer maxOrdersPerMonth;

    private Boolean isActive;

    // Campos gerados automaticamente - só para leitura
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
