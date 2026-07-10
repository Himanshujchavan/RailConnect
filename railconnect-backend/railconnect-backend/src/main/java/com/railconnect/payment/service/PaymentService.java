package com.railconnect.payment.service;

import com.railconnect.payment.dtorequestresponse.InvoiceResponse;
import com.railconnect.payment.dtorequestresponse.PaymentCallbackRequest;
import com.railconnect.payment.dtorequestresponse.PaymentHistoryResponse;
import com.railconnect.payment.dtorequestresponse.PaymentInitiateRequest;
import com.railconnect.payment.dtorequestresponse.PaymentInitiateResponse;
import com.railconnect.payment.dtorequestresponse.PaymentResponse;

public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request);

    PaymentResponse handleSuccess(PaymentCallbackRequest request);

    PaymentResponse handleFailure(PaymentCallbackRequest request);

    PaymentHistoryResponse getHistory(Long bookingId);

    InvoiceResponse getInvoice(Long paymentId);
}
