package com.faturium.service;

import com.faturium.config.exception.AppException;
import com.faturium.config.security.SecurityUtils;
import com.faturium.domain.model.Produto;
import com.faturium.domain.model.Status;
import com.faturium.domain.repository.OrganizacaoRepository;
import com.faturium.domain.repository.ProdutoRepository;
import com.faturium.dto.ProdutoDTO;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Code;
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
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final OrganizacaoRepository organizacaoRepository;
    private final CodeGenerator codeGenerator;
//    private final RedisService redisService;

    @Transactional
    public ProdutoDTO salvarProduto(ProdutoDTO produtoDTO){
        var organizacao = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Organização não encontrada",
                        "ID de organização inválido",
                        HttpStatus.NOT_FOUND));

        var code = codeGenerator.gerarProximoIdProduto(SecurityUtils.obterOrganizacaoId());

        var produto = Produto.builder()
                .id(produtoDTO.getId())
                .code(code)
                .prefix("PR")
                .nomeProduto(produtoDTO.getNomeProduto())
                .descricao(produtoDTO.getDescricao())
                .precoBase(produtoDTO.getPrecoBase())
                .status(Status.ATIVO)
                .organizacao(organizacao)
                .dataCriacao(LocalDateTime.now())
                .dataAlteracao(LocalDateTime.now())
                .build();

        produtoRepository.save(produto);
//        redisService.clearOrgCache("produtos", SecurityUtils.obterOrganizacaoId());

        return ProdutoDTO.builder()
                .id(produto.getId())
                .code(codeGenerator.getCodigoCompleto(produto.getPrefix(), produto.getCode()))
                .nomeProduto(produto.getNomeProduto())
                .descricao(produto.getDescricao())
                .precoBase(produto.getPrecoBase())
                .status(produto.getStatus())
                .organizacaoId(produto.getOrganizacao().getIdOrg())
                .dataCriacao(produto.getDataCriacao())
                .build();
    }

//    @Cacheable(value = "produtos", key = "T(com.faturium.config.security.SecurityUtils).obterOrganizacaoId() " +
//            "+ ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort",
//            condition = "#search == null || #search.isEmpty()")
    @Transactional(readOnly = true)
    public List<ProdutoDTO> buscarTodosProdutosOrganizacao(Pageable pageable, String search){
        if(search == null || search.isEmpty()) {
            return produtoRepository.findAll(SecurityUtils.obterOrganizacaoId(), pageable).stream().map(produto -> ProdutoDTO.builder()
                    .id(produto.getId())
                    .code(codeGenerator.getCodigoCompleto(produto.getPrefix(), produto.getCode()))
                    .nomeProduto(produto.getNomeProduto())
                    .descricao(produto.getDescricao())
                    .precoBase(produto.getPrecoBase())
                    .status(produto.getStatus())
                    .organizacaoId(produto.getOrganizacao().getIdOrg())
                    .dataCriacao(produto.getDataCriacao())
                    .dataAlteracao(produto.getDataAlteracao())
                    .build()).collect(Collectors.toList());
        }
        var searchForQuery = "%"+search+"%";
        return produtoRepository.findByName(SecurityUtils.obterOrganizacaoId(), searchForQuery, pageable).stream().map(produto -> ProdutoDTO.builder()
                .id(produto.getId())
                .code(codeGenerator.getCodigoCompleto(produto.getPrefix(), produto.getCode()))
                .nomeProduto(produto.getNomeProduto())
                .descricao(produto.getDescricao())
                .precoBase(produto.getPrecoBase())
                .status(produto.getStatus())
                .organizacaoId(produto.getOrganizacao().getIdOrg())
                .dataCriacao(produto.getDataCriacao())
                .dataAlteracao(produto.getDataAlteracao())
                .build()).collect(Collectors.toList());
    }

//    @CacheEvict(value = "produtos", key = "T(com.faturium.config.security.SecurityUtils).obterOrganizacaoId()")
    @Transactional
    public void deleteProduto(UUID id){
        var produto = produtoRepository.findById(id, SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Produto não encontrado",
                        "ID do produto inválido",
                        HttpStatus.NOT_FOUND));
//        if(!produto.getOrganizacao().getIdOrg().equals(SecurityUtils.obterOrganizacaoId())){
//            throw new AppException("Operação não permitida",
//                    "O produto não pertence à sua organização",
//                    HttpStatus.FORBIDDEN);
//        }
        produto.setDataAlteracao(LocalDateTime.now());
        produto.setStatus(Status.INATIVO);
        produtoRepository.save(produto);
//        redisService.clearOrgCache("produtos", SecurityUtils.obterOrganizacaoId());
    }

//    @CacheEvict(value = "produtos", key = "T(com.faturium.config.security.SecurityUtils).obterOrganizacaoId()")
    @Transactional
    public ProdutoDTO updateProduto(UUID id, ProdutoDTO produtoDTO){
        var produto = produtoRepository.findById(id, SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Produto não encontrado",
                        "ID do produto inválido",
                        HttpStatus.NOT_FOUND));

        Optional.ofNullable(produtoDTO.getNomeProduto())
                .filter(nome -> !nome.isEmpty())
                .ifPresent(produto::setNomeProduto);

        Optional.ofNullable(produtoDTO.getDescricao())
                .filter(descricao -> !descricao.isEmpty())
                .ifPresent(produto::setDescricao);

        Optional.ofNullable(produtoDTO.getPrecoBase())
                        .ifPresent(produto::setPrecoBase);

        produto.setDataAlteracao(LocalDateTime.now());

        produtoRepository.save(produto);
//        redisService.clearOrgCache("produtos", SecurityUtils.obterOrganizacaoId());

        return ProdutoDTO.builder()
                .id(produto.getId())
                .code(codeGenerator.getCodigoCompleto(produto.getPrefix(), produto.getCode()))
                .nomeProduto(produto.getNomeProduto())
                .descricao(produto.getDescricao())
                .precoBase(produto.getPrecoBase())
                .status(produto.getStatus())
                .dataCriacao(produto.getDataCriacao())
                .dataAlteracao(produto.getDataAlteracao())
                .build();
    }
}
