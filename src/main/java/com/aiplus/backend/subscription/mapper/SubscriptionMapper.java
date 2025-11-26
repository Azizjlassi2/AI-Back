package com.aiplus.backend.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.aiplus.backend.subscription.dto.SubscriptionDTO;
import com.aiplus.backend.subscription.dto.SubscriptionDetailsDTO;
import com.aiplus.backend.subscription.model.Subscription;
import com.aiplus.backend.users.mapper.AccountMapper;

import com.aiplus.backend.subscriptionPlans.mapper.SubscriptionPlanMapper;
import com.aiplus.backend.models.mapper.AiModelMapper;
import com.aiplus.backend.payment.mapper.PaymentMapper;

@Mapper(componentModel = "spring", uses = { AccountMapper.class, SubscriptionPlanMapper.class, PaymentMapper.class,
                AiModelMapper.class })
public interface SubscriptionMapper {

        @Mappings({ @Mapping(target = "client", source = "client"), @Mapping(target = "plan", source = "plan"),
                        @Mapping(target = "payment", source = "payment"),
                        @Mapping(target = "startDate", source = "startDate"),
                        @Mapping(target = "status", source = "status"),
                        @Mapping(target = "nextBillingDate", source = "nextBillingDate"),
                        @Mapping(target = "recurring", source = "recurring") })
        SubscriptionDetailsDTO toDetailsDto(Subscription entity);

        @Mappings({ @Mapping(target = "client", source = "client"), @Mapping(target = "plan", source = "plan"),
                        @Mapping(target = "payment", source = "payment"),
                        @Mapping(target = "startDate", source = "startDate"),
                        @Mapping(target = "status", source = "status"),
                        @Mapping(target = "nextBillingDate", source = "nextBillingDate"),
                        @Mapping(target = "recurring", source = "recurring"), @Mapping(target = "id", source = "id") })
        Subscription toEntity(SubscriptionDetailsDTO dto);

        SubscriptionDTO toDto(Subscription entity);
}
