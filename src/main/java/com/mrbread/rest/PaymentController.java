package com.mrbread.rest;

import com.mrbread.domain.model.Payment;
import com.mrbread.dto.PushinRequest;
import com.mrbread.dto.PushinResponse;
import com.mrbread.service.PushinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PushinService pushinService;

    @PreAuthorize("hasAuthority('ROLE_DEFAULT')")
    @PostMapping(value = "/payment/pix", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PushinResponse> createPix(@RequestBody PushinRequest pushinRequest){
        return ResponseEntity.ok().body(pushinService.createBill(pushinRequest));
    }

    @PostMapping(value = "/payment/pix/pushinpay", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> pushinPayWebHook(@RequestParam String id,
                                              @RequestParam Long value,
                                              @RequestParam String status,
                                              @RequestParam String end_to_end_id,
                                              @RequestParam String payer_name,
                                              @RequestParam String payer_national_registration){
        pushinService.processWebHook(id, value, status, end_to_end_id, payer_name, payer_national_registration);
        return ResponseEntity.ok("OK");
    }
}
