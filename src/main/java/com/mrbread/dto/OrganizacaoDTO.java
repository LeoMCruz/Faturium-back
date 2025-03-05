package com.mrbread.dto;

import com.mrbread.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacaoDTO {
    private Long id;
    private String nomeOrganizacao;
    private UUID idOrg;
    private String cnpj;
    private Set<UserDTO> usuarios;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
}
