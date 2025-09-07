package com.faturium.rest;

import com.faturium.dto.metrics.DashboardFaturamentoDTO;
import com.faturium.dto.metrics.VendasHojeDTO;
import com.faturium.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RequiredArgsConstructor
@RestController
public class DashboardController {
    private final DashboardService dashboardService;

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/dashboard", produces = "application/json")
    public ResponseEntity<DashboardFaturamentoDTO> dashboard(){
        try {
            DashboardFaturamentoDTO dados = dashboardService.obterDashboard();
            return ResponseEntity.ok(dados);
        } catch (IllegalStateException e) {
            log.warn("Usuário sem organização tentou acessar dashboard: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao obter dados de faturamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/dashboard/today", produces = "application/json")
    public ResponseEntity<VendasHojeDTO> previstoHoje(){
        try {
            VendasHojeDTO dados = dashboardService.previstoHoje();
            return ResponseEntity.ok(dados);
        } catch (IllegalStateException e) {
            log.warn("Usuário sem organização tentou acessar dashboard: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao obter dados de faturamento", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
