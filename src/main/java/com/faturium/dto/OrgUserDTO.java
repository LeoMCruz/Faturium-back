package com.faturium.dto;

import com.faturium.domain.model.AuthProvider;
import com.faturium.domain.model.PerfilAcesso;
import com.faturium.domain.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgUserDTO {
    private UUID id;
    private String username;
    private String nome;
    private Status status;
    private PerfilAcesso perfilAcesso;
    private String googleId;
    private AuthProvider authProvider;
    private Boolean profileComplete;
    private String nomeOrganizacao;
    private UUID organizacaoId;
    private String cnpj;
    private String endereco;
    private String telefone;
    private String cidade;
    private String estado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
}
