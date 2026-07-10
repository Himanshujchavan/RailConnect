package com.railconnect.payment.controller;

import com.railconnect.payment.dtorequestresponse.InvoiceResponse;
import com.railconnect.payment.dtorequestresponse.PaymentCallbackRequest;
import com.railconnect.payment.dtorequestresponse.PaymentHistoryResponse;
import com.railconnect.payment.dtorequestresponse.PaymentInitiateRequest;
import com.railconnect.payment.dtorequestresponse.PaymentInitiateResponse;
import com.railconnect.payment.dtorequestresponse.PaymentResponse;
import com.railconnect.payment.dtorequestresponse.RefundRequest;
import com.railconnect.payment.dtorequestresponse.RefundResponse;
import com.railconnect.payment.service.PaymentService;
import com.railconnect.payment.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Phase 4 — Payment Module.
 */
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;

    public PaymentController(PaymentService paymentService, RefundService refundService) {
        this.paymentService = paymentService;
        this.refundService = refundService;
    }

    @PostMapping("/payment/initiate")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(@Valid @RequestBody PaymentInitiateRequest request) {
        return new ResponseEntity<>(paymentService.initiatePayment(request), HttpStatus.CREATED);
    }

    @PostMapping("/payment/success")
    public ResponseEntity<PaymentResponse> paymentSuccess(@Valid @RequestBody PaymentCallbackRequest request) {
        return ResponseEntity.ok(paymentService.handleSuccess(request));
    }

    @PostMapping("/payment/failure")
    public ResponseEntity<PaymentResponse> paymentFailure(@Valid @RequestBody PaymentCallbackRequest request) {
        return ResponseEntity.ok(paymentService.handleFailure(request));
    }

    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> initiateRefund(@Valid @RequestBody RefundRequest request) {
        return new ResponseEntity<>(refundService.initiateRefund(request), HttpStatus.CREATED);
    }

    @GetMapping("/payment/history")
    public ResponseEntity<PaymentHistoryResponse> paymentHistory(@RequestParam Long bookingId) {
        return ResponseEntity.ok(paymentService.getHistory(bookingId));
    }

    @GetMapping("/payment/{paymentId}/invoice")
    public ResponseEntity<InvoiceResponse> invoice(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getInvoice(paymentId));
    }
}
