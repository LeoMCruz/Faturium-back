package com.mrbread.service;

import com.mrbread.config.exception.AppException;
import com.mrbread.domain.model.Produto;
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
        var organizacao = organizacaoRepository.findByIdOrg(produtoDTO.getOrganizacaoId())
                .orElseThrow(() -> new AppException("Organização não encontrada",
                        "ID de organização inválido",
                        HttpStatus.NOT_FOUND));

        var produto = Produto.builder()
                .id(produtoDTO.getId())
                .nomeProduto(produtoDTO.getNomeProduto())
                .descricao(produtoDTO.getDescricao())
                .precoBase(produtoDTO.getPrecoBase())
                .organizacao(organizacao)
                .build();
        produtoRepository.save(produto);
        return produtoDTO;
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> buscarTodosProdutosOrganizacao(UUID organizacaoId, Pageable pageable){
        return produtoRepository.findByOrganizacaoIdOrg(organizacaoId, pageable).stream().map(produto -> ProdutoDTO.builder()
                .id(produto.getId())
                .nomeProduto(produto.getNomeProduto())
                .descricao(produto.getDescricao())
                .precoBase(produto.getPrecoBase())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduto(Long id){
        produtoRepository.deleteById(id);
    }
}
