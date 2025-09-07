package com.mrbread.rest;

import com.mrbread.config.security.SecurityUtils;
import com.mrbread.service.PaymentSSEService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class PaymentSSEController {
    private final PaymentSSEService paymentSSEService;

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/sse/payment", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPaymentUpdates() {
        return paymentSSEService.createConnection();
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/test/sse", produces = "application/json")
    public ResponseEntity<String> testSSE() {
        UUID orgId = SecurityUtils.obterOrganizacaoId();

        // Testar notificação de pagamento processado
        paymentSSEService.notifyPaymentProcessed(orgId);

        return ResponseEntity.ok("Notificação de pagamento enviada para organização: " + orgId);
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/sse/close", produces = "application/json")
    public ResponseEntity<String> streamPaymentClose() {
        return ResponseEntity.ok().body(paymentSSEService.closeUserConnection());
    }

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @GetMapping(value = "/test/conections", produces = "application/json")
    public Integer qtd(){
        return paymentSSEService.getActiveConnections(SecurityUtils.obterOrganizacaoId());
    }
}
