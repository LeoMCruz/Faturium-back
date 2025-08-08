package com.mrbread.dto;

import com.mrbread.domain.model.PerfilAcesso;
import com.mrbread.domain.model.Status;
import jakarta.validation.constraints.NotBlank;
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
public class UserDTO {
    private UUID id;
    @NotBlank(message = "A senha é obrigatória")
    private String password;
    @NotBlank(message = "O email é obrigatório")
    private String username;
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    private String endereco;
    private String cidade;
    private String estado;
    private String telefone;
    private Status status;
    private PerfilAcesso perfilAcesso;
    private UUID organizacaoId;
    private String nomeOrganizacao;
    private String cnpj;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
}
