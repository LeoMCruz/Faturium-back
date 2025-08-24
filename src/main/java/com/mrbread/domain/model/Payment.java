package com.mrbread.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_criacao", referencedColumnName = "login")
    private User user;
    @Column
    private UUID idOrg;
    @Column
    private String pushinTransactionId;
    @Column
    private String copyPasteCode;
    @Column
    private String status;
    @Column
    private Long price;
    @Column
    private String endToendId;
    @Column
    private String payerName;
    @Column
    private String payerNationalRegistration;
}
