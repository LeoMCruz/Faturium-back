package com.mrbread.rest;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
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
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProdutoController {
    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping(value = "/produtos", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoDTO createProduct (@RequestBody ProdutoDTO produtoDTO){
        return produtoService.salvarProduto(produtoDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/produtos", produces = "application/json")
    public ResponseEntity<List<ProdutoDTO>> getProducts(@PageableDefault Pageable pageable){
        return ResponseEntity.ok(produtoService.buscarTodosProdutosOrganizacao(pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/produtos/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable UUID id){
        produtoService.deleteProduto(id);
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping(value = "/produtos/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ProdutoDTO> updateProdutct(@RequestBody ProdutoDTO produtoDTO, @PathVariable UUID id){
        return ResponseEntity.ok().body(produtoService.updateProduto(id, produtoDTO));
    }
}
