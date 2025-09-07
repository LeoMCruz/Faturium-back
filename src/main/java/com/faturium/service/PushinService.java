package com.faturium.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faturium.config.exception.AppException;
import com.faturium.config.security.SecurityUtils;
import com.faturium.domain.model.Payment;
import com.faturium.domain.model.Plan;
import com.faturium.domain.repository.PaymentRepository;
import com.faturium.domain.repository.PlanRepository;
import com.faturium.domain.repository.UserRepository;
import com.faturium.dto.PushinRequest;
import com.faturium.dto.PushinResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushinService {
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final OrganizationSubscriptionService organizationSubscriptionService;
    private final PaymentSSEService paymentSSEService;

    @Value("${TOKEN_PUSHIN}")
    private String tokenPushin;

    public PushinResponse createBill(PushinRequest pushinRequest) {

        try {
            String route = "https://4e48f55f3e4a.ngrok-free.app/payment/pix/pushinpay";
            log.info(pushinRequest.getPrice().toString());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            String jsonBody = String.format("{\"value\": %s, \"webhook_url\": \"%s\", \"split_rules\": []}",
                    pushinRequest.getPrice(),
                    route);
            RequestBody body = RequestBody.create(jsonBody, mediaType);
            Request request = new Request.Builder()
                    .url("https://api.pushinpay.com.br/api/pix/cashIn")
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + tokenPushin)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String responseBody = response.body().string();

                JsonNode jsonNode = objectMapper.readTree(responseBody);

                String transactionId = getStringValue(jsonNode, "id");
                String qrCode = getStringValue(jsonNode, "qr_code");
                String status = getStringValue(jsonNode, "status");

                var user = userRepository.findByLogin(SecurityUtils.getEmail()).orElseThrow(() -> new AppException(
                        "Erro", "Usuário não encontrado", HttpStatus.NOT_FOUND));

                var payment = Payment.builder()
                        .user(user)
                        .idOrg(SecurityUtils.obterOrganizacaoId())
                        .price(pushinRequest.getPrice())
                        .pushinTransactionId(transactionId)
                        .copyPasteCode(qrCode)
                        .status(status)
                        .build();

                paymentRepository.save(payment);
                return PushinResponse.builder()
                        .email(payment.getUser().getLogin())
                        .id(payment.getId())
                        .pushinTransactionId(payment.getPushinTransactionId())
                        .status(payment.getStatus())
                        .copyPasteCode(payment.getCopyPasteCode())
                        .idOrg(payment.getIdOrg())
                        .price(payment.getPrice())
                        .build();
            }

        } catch (Exception e) {
            throw new AppException("Erro ao criar cobrança PIX", e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    private String getStringValue(JsonNode jsonNode, String fieldName) {
        JsonNode field = jsonNode.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : null;
    }

    public void processWebHook(String id, Long value, String status, String end_to_end_id, String payer_name,
            String payer_national_registration) {
        System.out.println("id: " + id + " value: " + value + " status: " + status + " end_to_end_id: " + end_to_end_id
                + " payer_name: " + payer_name + " payer_national_registration: " + payer_national_registration);
        try {
            var payment = paymentRepository.findByPushinTransactionId(id).orElseThrow(() -> new AppException(
                    "Transação não encontrada ", "Essa transação não existe", HttpStatus.NOT_FOUND));

            if (status.equals("paid")) {
                if (payment.getPrice().equals(value)) {
                    payment.setStatus(status);
                    payment.setEndToendId(end_to_end_id);
                    payment.setPayerName(payer_name);
                    payment.setPayerNationalRegistration(payer_national_registration);

                    paymentRepository.save(payment);
                    createSubscriptonFromPayment(payment);
                    paymentSSEService.notifyPaymentProcessed(payment.getIdOrg());
                }
            } else {
                payment.setStatus(status);
                paymentRepository.save(payment);
            }

        } catch (Exception e) {
            log.error("Erro ao processar webhook: {}", e.getMessage(), e);
        }
    }

    private void createSubscriptonFromPayment(Payment payment) {
        try {
            var plan = getPlanByPrice(payment.getPrice());
            organizationSubscriptionService.createSubscription(payment, plan);

        } catch (Exception e) {
            throw new AppException("Erro ao criar assinatura", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public Plan getPlanByPrice(Long price) {
        BigDecimal priceInReais = BigDecimal.valueOf(price)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        System.out.println(priceInReais);
        return planRepository.findByPrice(priceInReais).orElseThrow(() -> new AppException(
                "Plano não encontrado", "Valor inválido", HttpStatus.NOT_FOUND));
    }
}
