package com.railconnect.payment.mapper;

import com.railconnect.entity.Transaction;
import com.railconnect.payment.dtorequestresponse.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "transactionId", source = "id")
    TransactionResponse toResponse(Transaction transaction);
}
