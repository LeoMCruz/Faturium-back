package com.faturium.rest;

import com.faturium.dto.DetalhesPedidoDTO;
import com.faturium.dto.PedidoDTO;
import com.faturium.dto.ResumoPedidoDTO;
import com.faturium.service.JasperReports.DetalhesPedidoReport;
import com.faturium.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;
    private final DetalhesPedidoReport reports;

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @PostMapping(value = "/pedidos", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createOrder(@RequestBody PedidoDTO pedidoDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criarPedido(pedidoDTO));
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/pedidos", produces = "application/json")
    public ResponseEntity<List<ResumoPedidoDTO>> getOrdersResume(@PageableDefault(sort = {"idPedido", "id"},
            direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(pedidoService.buscarResumoPedidos(pageable));
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/pedidos/{id}", produces = "application/json")
    public ResponseEntity<DetalhesPedidoDTO> getOrderById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(pedidoService.buscarPedidoPorId(id));
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @PutMapping(value = "/pedidos", produces = "application/json")
    public ResponseEntity<PedidoDTO> updateOrder(@RequestBody PedidoDTO pedidoDTO){
        return ResponseEntity.ok().body(pedidoService.alterarPedido(pedidoDTO));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/pedidos/{id}", produces = "application/json")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id){
        pedidoService.deletePedido(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @GetMapping(value = "/pedidos/reports/{id}", produces = "application/json")
    public ResponseEntity<Resource> orderByIdReport(@PathVariable UUID id) {
        try {
            DetalhesPedidoDTO dto = pedidoService.buscarPedidoPorId(id);
            String caminhoPdf = reports.generateReport(dto);
            Path path = Paths.get(caminhoPdf);
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }
            Resource pdf = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + path.getFileName().toString() + "\"")
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
