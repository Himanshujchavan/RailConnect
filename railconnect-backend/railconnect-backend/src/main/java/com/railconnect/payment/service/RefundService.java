package com.railconnect.payment.service;

import com.railconnect.payment.dtorequestresponse.RefundRequest;
import com.railconnect.payment.dtorequestresponse.RefundResponse;

public interface RefundService {

    RefundResponse initiateRefund(RefundRequest request);
}
