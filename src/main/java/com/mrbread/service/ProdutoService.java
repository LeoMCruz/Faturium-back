package com.mrbread.service;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.Produto;
import com.mrbread.domain.model.Status;
import com.mrbread.domain.repository.OrganizacaoRepository;
import com.mrbread.domain.repository.ProdutoRepository;
import com.mrbread.dto.ProdutoDTO;
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
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final OrganizacaoRepository organizacaoRepository;

    @Transactional
    public ProdutoDTO salvarProduto(ProdutoDTO produtoDTO){
        var organizacao = organizacaoRepository.findByIdOrg(SecurityUtils.obterOrganizacaoId())
                .orElseThrow(() -> new AppException("Organização não encontrada",
                        "ID de organização inválido",
                        HttpStatus.NOT_FOUND));

        var produto = Produto.builder()
                .id(produtoDTO.getId())
                .nomeProduto(produtoDTO.getNomeProduto())
                .descricao(produtoDTO.getDescricao())
                .precoBase(produtoDTO.getPrecoBase())
                .status(Status.ATIVO)
                .organizacao(organizacao)
                .build();

        produtoRepository.save(produto);

        return ProdutoDTO.builder()
                .id(produto.getId())
                .nomeProduto(produto.getNomeProduto())
                .descricao(produto.getDescricao())
                .precoBase(produto.getPrecoBase())
                .status(produto.getStatus())
                .organizacaoId(produto.getOrganizacao().getIdOrg())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> buscarTodosProdutosOrganizacao(Pageable pageable){
        return produtoRepository.findByOrganizacaoIdOrgAndStatus(SecurityUtils.obterOrganizacaoId(), Status.ATIVO, pageable).stream().map(produto -> ProdutoDTO.builder()
                .id(produto.getId())
                .nomeProduto(produto.getNomeProduto())
                .descricao(produto.getDescricao())
                .precoBase(produto.getPrecoBase())
                .status(produto.getStatus())
                .organizacaoId(produto.getOrganizacao().getIdOrg())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduto(UUID id){
        var produto = produtoRepository.findById(id)
                .orElseThrow(() -> new AppException("Produto não encontrado",
                        "ID do produto inválido",
                        HttpStatus.NOT_FOUND));
        if(!produto.getOrganizacao().getIdOrg().equals(SecurityUtils.obterOrganizacaoId())){
            throw new AppException("Operação não permitida",
                    "O produto não pertence à sua organização",
                    HttpStatus.FORBIDDEN);
        }
        produto.setStatus(Status.INATIVO);
        produtoRepository.save(produto);
    }

    @Transactional
    public ProdutoDTO updateProduto(UUID id, ProdutoDTO produtoDTO){
        if(id == null){
            throw new AppException("Produto não encontrado", "ID inválido", HttpStatus.BAD_REQUEST);
        }

        var getProduto = produtoRepository.findById(id);

        if(getProduto.isEmpty()){
            throw new AppException("Produto não encontrado", "ID inválido", HttpStatus.BAD_REQUEST);
        }

        Produto produto = getProduto.get();

        if(!produto.getOrganizacao().getIdOrg().equals(SecurityUtils.obterOrganizacaoId())){
            throw new AppException("Operação não permitida",
                    "O Produto não pertence à sua organização",
                    HttpStatus.BAD_REQUEST);
        }

        if(produtoDTO.getNomeProduto() != null && !produtoDTO.getNomeProduto().isEmpty()){
            produto.setNomeProduto(produtoDTO.getNomeProduto());
        }

        if(produtoDTO.getDescricao() != null && !produtoDTO.getDescricao().isEmpty()){
            produto.setDescricao(produtoDTO.getDescricao());
        }

        if(produtoDTO.getPrecoBase() != null){
            produto.setPrecoBase(produtoDTO.getPrecoBase());
        }

        if(produtoDTO.getStatus() !=null){
            produto.setStatus(produtoDTO.getStatus());
        }

        produtoRepository.save(produto);
        return ProdutoDTO.builder()
                .id(produto.getId())
                .nomeProduto(produto.getNomeProduto())
                .descricao(produto.getDescricao())
                .precoBase(produto.getPrecoBase())
                .status(produto.getStatus())
                .build();
    }
}
