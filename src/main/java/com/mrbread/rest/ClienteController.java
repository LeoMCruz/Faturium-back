package com.mrbread.rest;

import com.mrbread.dto.ClienteDTO;
import com.mrbread.service.ClienteService;
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
public class ClienteController {
    private final ClienteService clienteService;

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping(value = "/clientes", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createClient(@RequestBody ClienteDTO clienteDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.salvarCliente(clienteDTO));
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/clientes", produces = "application/json")
    public ResponseEntity<List<ClienteDTO>> getClients (@PageableDefault(sort = {"nomeFantasia", "id"},
            direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok().body(clienteService.buscarClientesOrganizacao(pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/clientes/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable UUID id){
        clienteService.deleteCliente(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping(value = "/clientes/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateClient(@PathVariable UUID id, @RequestBody ClienteDTO clienteDTO){
        return ResponseEntity.ok().body(clienteService.alteraCliente(id, clienteDTO));
    }
}
