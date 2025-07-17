package com.mrbread.dto;

import com.mrbread.domain.model.Organizacao;
import com.mrbread.domain.model.Status;
import com.mrbread.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
