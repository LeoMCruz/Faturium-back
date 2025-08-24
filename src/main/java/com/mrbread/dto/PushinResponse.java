package com.mrbread.dto;

import com.mrbread.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
