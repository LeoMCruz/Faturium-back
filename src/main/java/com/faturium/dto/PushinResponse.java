package com.faturium.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushinResponse {
    private UUID id;
    private String email;
    private UUID idOrg;
    private String pushinTransactionId;
    private String copyPasteCode;
    private String status;
    private Long price;
}
