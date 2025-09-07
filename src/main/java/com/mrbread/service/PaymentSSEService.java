package com.mrbread.service;

import com.mrbread.config.exception.AppException;
import com.mrbread.config.security.SecurityUtils;
import com.mrbread.domain.repository.UserRepository;
import com.mrbread.dto.PaymentNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSSEService {
    private final Map<UUID, Set<SseEmitter>> orgEmitters = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final Map<UUID, String> userToSessionMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> sessionToOrgMap = new ConcurrentHashMap<>();

    private static final long SSE_TIMEOUT = 9999999 * 60 * 1000L;

    public SseEmitter createConnection() {
        var orgId = SecurityUtils.obterOrganizacaoId();
        var userEmail = SecurityUtils.getEmail();
        var user = userRepository.findByLogin(userEmail)
                .orElseThrow(() -> new AppException("User not found", "", HttpStatus.BAD_REQUEST));

        UUID userId = user.getId();
        String sessionId = UUID.randomUUID().toString();

        // Salvar mapeamentos
        userToSessionMap.put(userId, sessionId);
        sessionToOrgMap.put(sessionId, orgId);

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        orgEmitters.computeIfAbsent(orgId, k -> new CopyOnWriteArraySet<>()).add(emitter);

        // Callbacks simplificados
        emitter.onCompletion(() -> cleanup(sessionId, emitter));
        emitter.onTimeout(() -> cleanup(sessionId, null));
        emitter.onError(ex -> cleanup(sessionId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connection").data("Conectado"));
        } catch (IOException e) {
            cleanup(sessionId, emitter);
        }

        log.info("SSE estabelecido para usuário {} org {} session {}", userId, orgId, sessionId);
        return emitter;
    }

    // Metodo principal de notificação - fecha conexão após pagamento
    public void notifyPaymentProcessed(UUID orgId) {
        sendNotificationAndClose(orgId, "payment-processed", "Pagamento processado com sucesso!");
    }

    public void notifyPaymentFailed(UUID orgId) {
        sendNotification(orgId, "payment-failed", "Pagamento não processado.");
    }

    public void notifySubscriptionUpdated(UUID orgId) {
        sendNotification(orgId, "subscription-updated", "Assinatura atualizada!");
    }

    // Metodo unificado para envio
    private void sendNotification(UUID orgId, String eventType, String message) {
        Set<SseEmitter> emitters = orgEmitters.get(orgId);
        if (emitters == null || emitters.isEmpty()) return;

        PaymentNotificationDTO notification = PaymentNotificationDTO.builder()
                .message(message)
                .eventType(eventType)
                .build();

        Set<SseEmitter> copy = new CopyOnWriteArraySet<>(emitters);
        copy.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name(eventType).data(notification));
            } catch (IOException e) {
                removeEmitter(orgId, emitter);
            }
        });

        log.info("Notificação '{}' enviada para {} conexões org {}", eventType, copy.size(), orgId);
    }

    // Enviar e fechar (para pagamento processado)
    private void sendNotificationAndClose(UUID orgId, String eventType, String message) {
        Set<SseEmitter> emitters = orgEmitters.get(orgId);
        if (emitters == null || emitters.isEmpty()) return;

        PaymentNotificationDTO notification = PaymentNotificationDTO.builder()
                .message(message)
                .eventType(eventType)
                .build();

        Set<SseEmitter> copy = new CopyOnWriteArraySet<>(emitters);
        copy.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name(eventType).data(notification));
//                emitter.complete();
            } catch (IOException e) {
                log.warn("Erro ao enviar/fechar SSE: {}", e.getMessage());
            } finally {
                removeEmitter(orgId, emitter);
            }
        });

        log.info("Notificação '{}' enviada e {} conexões fechadas org {}", eventType, copy.size(), orgId);
    }

    // Cleanup unificado
    private void cleanup(String sessionId, SseEmitter emitter) {
        try {
            UUID orgId = sessionToOrgMap.remove(sessionId);
            userToSessionMap.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));

            if (orgId != null && emitter != null) {
                removeEmitter(orgId, emitter);
            }
        } catch (Exception e) {
            log.trace("Cleanup error (ignored): {}", e.getMessage());
        }
    }

    private void removeEmitter(UUID orgId, SseEmitter emitter) {
        try {
            Set<SseEmitter> emitters = orgEmitters.get(orgId);
            if (emitters != null) {
                emitters.remove(emitter);
                if (emitters.isEmpty()) {
                    orgEmitters.remove(orgId);
                }
            }
        } catch (Exception e) {
            log.trace("Remove emitter error (ignored)");
        }
    }

    public String closeUserConnection() {
        var userEmail = SecurityUtils.getEmail();
        var user = userRepository.findByLogin(userEmail)
                .orElseThrow(() -> new AppException("User not found", "", HttpStatus.BAD_REQUEST));

        String sessionId = userToSessionMap.get(user.getId());
        if (sessionId != null) {
            cleanup(sessionId, null);
        }
        return "OK";
    }

    // Métodos utilitários essenciais
    public int getActiveConnections(UUID orgId) {
        Set<SseEmitter> emitters = orgEmitters.get(orgId);
        return emitters != null ? emitters.size() : 0;
    }

    public int getTotalActiveConnections() {
        return orgEmitters.values().stream().mapToInt(Set::size).sum();
    }

    public boolean isUserConnected(UUID userId) {
        return userToSessionMap.containsKey(userId);
    }
}
