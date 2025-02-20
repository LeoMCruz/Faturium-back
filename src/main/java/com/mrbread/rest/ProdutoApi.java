package com.mrbread.rest;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.model.PerfilAcesso;
import com.mrbread.domain.repository.ProdutoRepository;
import com.mrbread.dto.ProdutoDTO;
import com.mrbread.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProdutoApi {
    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;

    @PostMapping(value = "/produtos", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createProduct (@RequestBody ProdutoDTO produtoDTO){
        if(!SecurityUtils.isAdmin() && !SecurityUtils.isManager()){
            throw new AppException("Operação não permitida",
                    "O usuário não possui permissão para adicionar produtos",
                    HttpStatus.FORBIDDEN);
        }
        produtoDTO.setOrganizacaoId(SecurityUtils.obterOrganizacaoId());
        return ResponseEntity.status(HttpStatus.OK).body(produtoService.salvarProduto(produtoDTO));
    }

    @GetMapping(value = "/produtos", produces = "application/json")
    public ResponseEntity<List<ProdutoDTO>> getProducts(@PageableDefault Pageable pageable){
        return ResponseEntity.ok(produtoService.buscarTodosProdutosOrganizacao(SecurityUtils.obterOrganizacaoId(), pageable));
    }

    @DeleteMapping(value = "/produtos/{id}", produces = "application/json")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        if(!SecurityUtils.isAdmin()){
            throw new AppException("Operação não permitida",
                    "O usuário não possui permissão para deletar",
                    HttpStatus.FORBIDDEN);
        }
        var produto = produtoRepository.findById(id)
            .orElseThrow(() -> new AppException("Produto não encontrado",
                "ID do produto inválido",
                HttpStatus.NOT_FOUND));
        if(!produto.getOrganizacao().getId().equals(SecurityUtils.obterOrganizacaoId())){
            throw new AppException("Operação não permitida",
                    "O produto não pertence à sua organização",
                    HttpStatus.FORBIDDEN);
        }
        produtoService.deleteProduto(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
