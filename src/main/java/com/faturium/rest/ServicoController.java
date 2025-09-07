package com.faturium.rest;

import com.faturium.dto.ServicoDTO;
import com.faturium.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ServicoController {
    private final ServicoService servicoService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping(value = "/servicos", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ServicoDTO> createService(@RequestBody ServicoDTO servicoDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.salvarServico(servicoDTO));
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/servicos", produces = "application/json")
    public ResponseEntity<List<ServicoDTO>> getServices(@PageableDefault(sort = {"nomeServico", "id"},
            direction = Sort.Direction.ASC) Pageable pageable, @RequestParam(required = false) String search){
        return ResponseEntity.ok().body(servicoService.buscarServicosOrganizacao(pageable, search));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/servicos/{id}", produces = "application/json")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id){
        servicoService.deleteServico(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping(value = "/servicos/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ServicoDTO> updateService(@RequestBody ServicoDTO servicoDTO, @PathVariable UUID id){
        return ResponseEntity.ok().body(servicoService.alteraServico(id, servicoDTO));
    }

}
