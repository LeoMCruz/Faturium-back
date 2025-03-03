package com.mrbread.service;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.Servico;
import com.mrbread.domain.model.Status;
import com.mrbread.domain.repository.OrganizacaoRepository;
import com.mrbread.domain.repository.ServicoRepository;
import com.mrbread.dto.ServicoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {
    private final ServicoRepository servicoRepository;
    private final OrganizacaoRepository organizacaoRepository;

    @Transactional
    public ServicoDTO salvarServico(ServicoDTO servicoDTO){
        var organizacao = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Organização não encontrada",
                        "ID de organização inválido",
                        HttpStatus.NOT_FOUND));

        var servico = Servico.builder()
                .id(servicoDTO.getId())
                .nomeServico(servicoDTO.getNomeServico())
                .descricao(servicoDTO.getDescricao())
                .precoBase(servicoDTO.getPrecoBase())
                .status(Status.ATIVO)
                .organizacao(organizacao)
                .build();

        servicoRepository.save(servico);

        return ServicoDTO.builder()
                .id(servico.getId())
                .nomeServico(servico.getNomeServico())
                .descricao(servico.getDescricao())
                .precoBase(servico.getPrecoBase())
                .status(servico.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ServicoDTO> buscarServicosOrganizacao(Pageable pageable){
        return servicoRepository.findByOrganizacaoIdOrgAndStatus(SecurityUtils.obterOrganizacaoId(),Status.ATIVO ,pageable).stream().map(
                servico -> ServicoDTO.builder()
                        .id(servico.getId())
                        .nomeServico(servico.getNomeServico())
                        .descricao(servico.getDescricao())
                        .precoBase(servico.getPrecoBase())
                        .status(servico.getStatus())
                        .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public void deleteServico(UUID id){
        var servico = servicoRepository.findById(id)
                .orElseThrow(() -> new AppException("Serviço não encontrado",
                        "ID do serviço é inválido",
                        HttpStatus.NOT_FOUND));
        if(!servico.getOrganizacao().getIdOrg().equals(SecurityUtils.obterOrganizacaoId())){
            throw new AppException("Operação não permitida",
                    "O serviço não pertence à sua organização",
                    HttpStatus.FORBIDDEN);
        }
        servico.setStatus(Status.INATIVO);
        servicoRepository.save(servico);
    }

    @Transactional
    public ServicoDTO alteraServico(UUID id, ServicoDTO servicoDTO){
        if(id == null){
            throw new AppException("Servico não encontrado", "ID inválido", HttpStatus.BAD_REQUEST);
        }

        var getServico = servicoRepository.findById(id);

        if(getServico.isEmpty()){
            throw new AppException("Servico não encontrado", "ID inválido", HttpStatus.BAD_REQUEST);
        }

        Servico servico = getServico.get();

        if(!servico.getOrganizacao().getIdOrg().equals(SecurityUtils.obterOrganizacaoId())){
            throw new AppException("Operação não permitida",
                    "O Serviço não pertence à sua organização",
                    HttpStatus.BAD_REQUEST);
        }

        if(servicoDTO.getNomeServico() != null && !servico.getNomeServico().isEmpty()){
            servico.setNomeServico(servicoDTO.getNomeServico());
        }

        if(servicoDTO.getDescricao() != null && !servicoDTO.getDescricao().isEmpty()){
            servico.setDescricao(servicoDTO.getDescricao());
        }

        if(servicoDTO.getPrecoBase() != null){
            servico.setPrecoBase(servicoDTO.getPrecoBase());
        }

        if(servicoDTO.getStatus() != null){
            servico.setStatus(servicoDTO.getStatus());
        }

        servicoRepository.save(servico);

        return ServicoDTO.builder()
                .id(servico.getId())
                .nomeServico(servico.getNomeServico())
                .descricao(servico.getDescricao())
                .status(servico.getStatus())
                .precoBase(servico.getPrecoBase())
                .build();
    }

}
