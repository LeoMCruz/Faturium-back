package com.faturium.dto;

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
public class ClienteDTO {
    private UUID id;
    private String nomeFantasia;
    private String razaoSocial;
    private String cnpj;
    private String endereco;
    private String cidade;
    private String estado;
    private String email;
    private String telefone;
    private UUID organizacao;
    private String usuarioCriacao;
    private Status status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAlteracao;
}
