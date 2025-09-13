package com.faturium.service;

import com.faturium.config.exception.AppException;
import com.faturium.config.security.SecurityUtils;
import com.faturium.domain.model.Organizacao;
import com.faturium.domain.repository.OrganizacaoRepository;
import com.faturium.dto.OrganizacaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrganizacaoService {
    private final OrganizacaoRepository organizacaoRepository;

    @Transactional
    public OrganizacaoDTO atualizarOrganizacao(OrganizacaoDTO organizacaoDTO){
        var organizacao = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId()).orElseThrow( () -> new AppException(
                "ID inválido",
                "Organização não encontrada",
                HttpStatus.BAD_REQUEST
        ));

        if(organizacaoDTO.getNomeOrganizacao() != null && !organizacaoDTO.getNomeOrganizacao().isEmpty()) {
            organizacao.setNomeOrganizacao(organizacaoDTO.getNomeOrganizacao());
        }
        if(organizacaoDTO.getEstado() != null && !organizacaoDTO.getEstado().isEmpty()){
            organizacao.setEstado(organizacaoDTO.getEstado());
        }
        if(organizacaoDTO.getCidade() != null && !organizacaoDTO.getCidade().isEmpty()){
            organizacao.setCidade(organizacaoDTO.getCidade());
        }
        if(organizacaoDTO.getEndereco() != null && !organizacaoDTO.getEndereco().isEmpty()){
            organizacao.setEndereco(organizacaoDTO.getEndereco());
        }
        if(organizacaoDTO.getTelefone() != null && !organizacaoDTO.getTelefone().isEmpty()){
            organizacao.setTelefone(organizacaoDTO.getTelefone());
        }
        organizacao.setDataAlteracao(LocalDateTime.now());
        organizacaoRepository.save(organizacao);
        return OrganizacaoDTO.builder()
                .nomeOrganizacao(organizacao.getNomeOrganizacao())
                .telefone(organizacao.getTelefone())
                .estado(organizacao.getEstado())
                .cidade(organizacao.getCidade())
                .endereco(organizacao.getEndereco())
                .build();
    }

}
