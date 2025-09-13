package com.faturium.dto;

import com.faturium.domain.model.PerfilAcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private UUID id;
    private String username;
    private String password;
    private String newPassword;
    private String nome;
    private PerfilAcesso perfilAcesso;
}
