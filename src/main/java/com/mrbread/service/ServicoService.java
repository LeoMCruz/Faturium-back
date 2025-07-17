package com.mrbread.service;

import com.mrbread.config.cache.RedisService;
import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.Servico;
import com.mrbread.domain.model.Status;
import com.mrbread.domain.repository.OrganizacaoRepository;
import com.mrbread.domain.repository.ServicoRepository;
import com.mrbread.dto.ServicoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {
    private final ServicoRepository servicoRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final RedisService redisService;

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
                .dataCriacao(LocalDateTime.now())
                .dataAlteracao(LocalDateTime.now())
                .build();

        servicoRepository.save(servico);
        redisService.clearOrgCache("servicos", SecurityUtils.obterOrganizacaoId());

        return ServicoDTO.builder()
                .id(servico.getId())
                .nomeServico(servico.getNomeServico())
                .descricao(servico.getDescricao())
                .precoBase(servico.getPrecoBase())
                .status(servico.getStatus())
                .dataCriacao(servico.getDataCriacao())
                .build();
    }

    @Cacheable(value = "servicos", key = "T(com.mrbread.config.security.SecurityUtils).obterOrganizacaoId() " +
            "+ ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
            condition = "#search == null || #search.isEmpty()")
    @Transactional(readOnly = true)
    public List<ServicoDTO> buscarServicosOrganizacao(Pageable pageable, String search){
        if(search == null || search.isEmpty()) {
            return servicoRepository.findAll(SecurityUtils.obterOrganizacaoId(), pageable).stream().map(
                    servico -> ServicoDTO.builder()
                            .id(servico.getId())
                            .nomeServico(servico.getNomeServico())
                            .descricao(servico.getDescricao())
                            .precoBase(servico.getPrecoBase())
                            .status(servico.getStatus())
                            .dataCriacao(servico.getDataCriacao())
                            .dataAlteracao(servico.getDataAlteracao())
                            .build()
            ).collect(Collectors.toList());
        }
        return buscarNome(search);

    }

    private List<ServicoDTO> buscarNome(String search){
        var searchForQuery = "%"+search+"%";
        return servicoRepository.findByName(SecurityUtils.obterOrganizacaoId(), searchForQuery).stream().map(
                servico -> ServicoDTO.builder()
                        .id(servico.getId())
                        .nomeServico(servico.getNomeServico())
                        .descricao(servico.getDescricao())
                        .precoBase(servico.getPrecoBase())
                        .status(servico.getStatus())
                        .dataCriacao(servico.getDataCriacao())
                        .dataAlteracao(servico.getDataAlteracao())
                        .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public void deleteServico(UUID id){
        var servico = servicoRepository.findById(id, SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Serviço não encontrado",
                        "ID do serviço é inválido",
                        HttpStatus.NOT_FOUND));
        servico.setDataAlteracao(LocalDateTime.now());
        servico.setStatus(Status.INATIVO);
        servicoRepository.save(servico);
        redisService.clearOrgCache("servico", SecurityUtils.obterOrganizacaoId());
    }

    @Transactional
    public ServicoDTO alteraServico(UUID id, ServicoDTO servicoDTO){
        var servico = servicoRepository.findById(id, SecurityUtils.obterOrganizacaoId()).orElseThrow(() -> new AppException(
                "Serviço não encontrado",
                "ID do serviço é inválido",
                HttpStatus.NOT_FOUND));

        Optional.ofNullable(servicoDTO.getNomeServico())
                        .filter(nome -> !nome.isEmpty())
                        .ifPresent(servico::setNomeServico);

        Optional.ofNullable(servicoDTO.getDescricao())
                        .filter(descricao -> !descricao.isEmpty())
                        .ifPresent(servico::setDescricao);

        Optional.ofNullable(servicoDTO.getPrecoBase())
                        .ifPresent(servico::setPrecoBase);


        servico.setDataAlteracao(LocalDateTime.now());

        servicoRepository.save(servico);
        redisService.clearOrgCache("servico", SecurityUtils.obterOrganizacaoId());

        return ServicoDTO.builder()
                .id(servico.getId())
                .nomeServico(servico.getNomeServico())
                .descricao(servico.getDescricao())
                .status(servico.getStatus())
                .precoBase(servico.getPrecoBase())
                .dataCriacao(servico.getDataCriacao())
                .dataAlteracao(servico.getDataAlteracao())
                .build();
    }

}
