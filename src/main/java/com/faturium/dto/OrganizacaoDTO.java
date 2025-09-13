package com.faturium.dto;

import com.faturium.domain.model.Status;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoDTO {
    private String nomeOrganizacao;
    private UUID idOrg;
    private String endereco;
    private String cidade;
    private String estado;
    private String telefone;
    private LocalDateTime dataAltercao;
}
