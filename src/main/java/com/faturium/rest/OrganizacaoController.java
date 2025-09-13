package com.faturium.rest;

import com.faturium.dto.OrganizacaoDTO;
import com.faturium.service.OrganizacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class OrganizacaoController {
    private final OrganizacaoService organizacaoService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/organizacao", consumes = "application/json", produces = "application/json")
    public ResponseEntity<OrganizacaoDTO> updateOrg(@RequestBody OrganizacaoDTO organizacaoDTO){
        return ResponseEntity.ok().body(organizacaoService.atualizarOrganizacao(organizacaoDTO));
    }
}
