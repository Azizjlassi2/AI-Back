package com.aiplus.backend.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.Mapping;

import com.aiplus.backend.payment.dto.PaymentSummaryDTO;
import com.aiplus.backend.payment.model.Payment;
import com.aiplus.backend.users.mapper.AccountMapper;

@Mapper(componentModel = "spring", uses = { AccountMapper.class })
public interface PaymentMapper {

        @Mappings({ @Mapping(target = "id", source = "id"),
                        @Mapping(target = "transactionId", source = "transactionId"),
                        @Mapping(target = "orderId", source = "orderId"),
                        @Mapping(target = "amount", source = "amount"),
                        @Mapping(target = "currency", source = "currency"),
                        @Mapping(target = "gateway", source = "gateway"),
                        @Mapping(target = "status", source = "status"),
                        @Mapping(target = "createdAt", source = "createdAt"),
                        @Mapping(target = "updatedAt", source = "updatedAt"),
                        @Mapping(target = "completedAt", source = "completedAt") })
        PaymentSummaryDTO toDto(Payment entity);

        @Mappings({ @Mapping(target = "id", source = "id"),
                        @Mapping(target = "transactionId", source = "transactionId"),
                        @Mapping(target = "orderId", source = "orderId"),
                        @Mapping(target = "amount", source = "amount"),
                        @Mapping(target = "currency", source = "currency"),
                        @Mapping(target = "gateway", source = "gateway"),
                        @Mapping(target = "status", source = "status"),
                        @Mapping(target = "createdAt", source = "createdAt"),
                        @Mapping(target = "updatedAt", source = "updatedAt"),
                        @Mapping(target = "completedAt", source = "completedAt") })
        Payment toEntity(PaymentSummaryDTO dto);
}